import sys

from twisted.internet.protocol import Factory
from twisted.protocols.basic import LineReceiver

import bankjson as bjson
from db import DB
from log import Log

class BasicBankProtocol(LineReceiver):
    def __init__(self, handlers):
        self.handlers = handlers
    
    def connectionMade(self):
        self.sendLine('{}')

    def connectionLost(self, reason):
        pass

    def lineReceived(self, line):
        try:
            request = bjson.loads(line)
        except ValueError as e:
            print e.message
        else:
            Log.debug("handling " + str(request))
            if not self._check_credentials(request):
                self.fail("Invalid credentials")
            if 'method' in request:
                if request['method'] not in self.handlers:
                    self.fail('No handler for method: ' + request['method'])
                else:
                    self.handlers[request['method']](self, request)
            else:
                self.fail("No method")

    def _check_credentials(self, request):
        # TODO: check token
        return True

    def fail(self, reason):
        self.sendLine(bjson.dumps('{error: "' + reason + '}'))
        self.transport.loseConnection()


def handlermethod(func):
    def wrap(self, protocol, request):
        func(self, protocol, request)
        protocol.transport.loseConnection()
    return wrap


class BrutalBankFactory(Factory):
    def __init__(self):
        self.handlers = {}
        self.handlers['list_services'] = self.list_services
        self.handlers['list_buyable_services'] = self.list_buyable_services
        self.handlers['request_service'] = self.request_service
        self.db = DB()

    @handlermethod
    def list_services(self, protocol, request):
        if 'id' not in request:
            protocol.fail("No id")
        protocol.sendLine(
                bjson.dumps(self.db.get_services(request['id'])))
        
    @handlermethod
    def list_buyable_services(self, protocol, request):
        if 'id' not in request:
            protocol.fail("No id")
        protocol.sendLine(bjson.dumps(self.db.get_buyable_services(request['id'])))

    @handlermethod
    def request_service(self, protocol, request):
        if 'id' not in request:
            protocol.fail("No id")
        protocol.fail('Not enough')

    def buildProtocol(self, addr):
        return BasicBankProtocol(self.handlers)
