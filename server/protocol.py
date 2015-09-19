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
                return bjson.dumps({'error': 'No msg'})
            msg = bjson.loads(request.args['msg'][0])
        except ValueError as e:
            print e.message
        else:
            Log.debug("handling " + str(msg))
            if not self._check_credentials(msg):
                return bjson.dumps({'error': "Invalid credentials"})
            if 'method' in msg:
                if msg['method'] not in self.handlers:
                    return bjson.dumps({'error': 'No handler for method: ' + msg['method']})
                else:
                    return self.handlers[msg['method']](self, msg)
            else:
                return bjson.dumps({'error': "No method"})

    def _check_credentials(self, request):
        # TODO: check token
        return True


def handlermethod(func):
    def wrap(self, protocol, request):
        if not ('id' in request or ('login' in request and 'pass' in request)):
            return bjson.dumps({'error': 'No credentials'})
        try:
            return bjson.dumps(func(self, protocol, request))
        except InvalidId:
            return bjson.dumps({'error': 'Invalid login'})
    return wrap


def store_in_history(func):
    def wrap(self, protocol, request):
        output = func(self, protocol, request)
        self.db.store_in_history(self._get_user_id(request), output, request)
        return output
    return wrap


class BrutalBankProtocol(BasicBankProtocol):
    def __init__(self):
        self.handlers = {}
        self.handlers['list_services'] = self.list_services
        self.handlers['list_buyable_services'] = self.list_buyable_services
        self.handlers['request_service'] = self.request_service
        self.handlers['request_loan'] = self.request_loan
        self.handlers['buy_service'] = self.buy_service
        self.handlers['user_data'] = self.user_data
        self.handlers['request_history'] = self.request_history
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
    @store_in_history
    def request_service(self, protocol, request):
        id_ = self._get_user_id(request)
        if 'service_id' not in request:
            return {'error': 'No service id'}
        return self.db.request_service(id_, request['service_id'])

    @handlermethod
    def user_data(self, protocol, request):
        id_ = self._get_user_id(request)
        return self.db.get_user_info(id_)

    @handlermethod
    @store_in_history
    def buy_service(self, protocol, request):
        id_ = self._get_user_id(request)
        if 'service_id' not in request:
            return {'error': 'No service id'}
        return self.db.buy_service(id_, request['service_id'])

    @handlermethod
    @store_in_history
    def request_loan(self, protocol, request):
        id_ = self._get_user_id(request)
        if 'amount' not in request:
            return {'error': 'No amount'}
        return self.db.request_loan(id_, request['amount'])

    @handlermethod
    def request_history(self, protocol, request):
        id_ = self._get_user_id(request)
        return self.db.request_history(id_)

    def _get_user_id(self, request):
        if 'id' in request and request['id'] in self.db.users.keys():
            return request['id']
        return self.db.get_user_id(request['login'], request['pass']).id_
