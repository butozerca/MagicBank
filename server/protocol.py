import sys

from twisted.web.resource import Resource

import bankjson as bjson
from db import DB, InvalidId
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
        return bjson.dumps('{error: ' + reason + '}')


def handlermethod(func):
    def wrap(self, protocol, request):
        if not ('id' in request or ('login' in request and 'pass' in request)):
            return protocol.fail('No credentials')
        try:
            return bjson.dumps(func(self, protocol, request))
        except InvalidId:
            return protocol.fail('Invalid login')
    return wrap


class BrutalBankProtocol(BasicBankProtocol):
    def __init__(self):
        self.handlers = {}
        self.handlers['list_services'] = self.list_services
        self.handlers['list_buyable_services'] = self.list_buyable_services
        self.handlers['request_service'] = self.request_service
        self.handlers['user_data'] = self.user_data
        self.db = DB()

    @handlermethod
    def list_services(self, protocol, request):
        id_ = self._get_user_id(request)
        return self.db.get_services(id_)
        
    @handlermethod
    def list_buyable_services(self, protocol, request):
        id_ = self._get_user_id(request)
        return self.db.get_buyable_services(id_)

    @handlermethod
    def request_service(self, protocol, request):
        id_ = self._get_user_id(request)
        if 'service_id' not in request:
            return protocol.fail('No service id')
        return self.db.request_service(id_, request['service_id'])

    @handlermethod
    def user_data(self, protocol, request):
        id_ = self._get_user_id(request)
        return self.db.get_user_info(id_)

    @handlermethod
    def buy_service(self, protocol, request):
        id_ = self._get_user_id(request)
        if 'service_id' not in request:
            return protocol.fail('No service id')
        return self.db.request_service(id_, request['service_id'])

    def _get_user_id(self, request):
        if 'id' in request and request['id'] in self.db.users.keys():
            return request['id']
        return self.db.get_user_id(request['login'], request['pass']).id_
