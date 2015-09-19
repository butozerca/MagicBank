import sys

from twisted.web.resource import Resource

import bankjson as bjson
from db import DB
from log import Log

class BasicBankProtocol(Resource):
    isLeaf = True
    def render_GET(self, request):
        try:
            if 'msg' not in request.args:
                return self.fail("No msg")
            msg = bjson.loads(request.args['msg'][0])
        except ValueError as e:
            print e.message
        else:
            Log.debug("handling " + str(msg))
            if not self._check_credentials(msg):
                return self.fail("Invalid credentials")
            if 'method' in msg:
                if msg['method'] not in self.handlers:
                    return self.fail('No handler for method: ' + msg['method'])
                else:
                    return self.handlers[msg['method']](self, msg)
            else:
                return self.fail("No method")

    def _check_credentials(self, request):
        # TODO: check token
        return True

    def fail(self, reason):
        return bjson.dumps('{error: "' + reason + '}')


def handlermethod(func):
    def wrap(self, protocol, request):
        return func(self, protocol, request)
    return wrap


class BrutalBankProtocol(BasicBankProtocol):
    def __init__(self):
        self.handlers = {}
        self.handlers['list_services'] = self.list_services
        self.handlers['list_buyable_services'] = self.list_buyable_services
        self.handlers['request_service'] = self.request_service
        self.db = DB()

    @handlermethod
    def list_services(self, protocol, request):
        if 'id' not in request:
            return protocol.fail("No id")
        return bjson.dumps(self.db.get_services(request['id']))
        
    @handlermethod
    def list_buyable_services(self, protocol, request):
        if 'id' not in request:
            return protocol.fail("No id")
        return bjson.dumps(self.db.get_buyable_services(request['id']))

    @handlermethod
    def request_service(self, protocol, request):
        if 'id' not in request:
            return protocol.fail("No id")
        return protocol.fail('Not enough')
