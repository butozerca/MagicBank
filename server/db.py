#!/usr/bin/env python
# -*- coding: utf-8 -*-
import copy
from collections import defaultdict

from service import Service
from user import User
from os import urandom
from base64 import b64encode


class DB:
    def __init__(self):
        self.users = {
            'soperkrulDupa.8': User('soperkrul', 'Dupa.8', 'Jakub', 'Król',
            'krol@jest.spoko', 1337, 666, 0, 'Economy Saver Negative',
            {
                '0': Service('0', 'Hydraulik', 5, 50, 'wklada rury', 200, 45),
                '1': Service('1', 'Ginekolog', 12, 100, 'wyjmuje rury', 200, 45),
                '2': Service('2', 'Holowanie', 7, 200, 'ciagnie rure', 300, 45),
            },
            {
                '0': Service('0', 'Hydraulik', 5, 50, 'wklada rury', 200, 45),
                '1': Service('1', 'Ginekolog', 12, 100, 'wyjmuje rury', 200, 45),
                '2': Service('2', 'Holowanie', 7, 200, 'ciagnie rure', 300, 45),
                '3': Service('3','Grzyby', 10, 10, 'grzyb', 800, 45),
            })
        }
        self.histories = defaultdict(list)

    def get_services(self, id_):
        return self.users[id_].services.values()

    def get_buyable_services(self, id_):
        return self.users[id_].buyable.values()
                
    def get_user_info(self, id_):
        if id_ in self.users:
            return self.users[id_]

    def buy_service(self, id_, service_id):
        services = self.users[id_].buyable
        if service_id in services:
            new_service = services[service_id]
            if new_service.price > self.users[id_].money:
                return {'error': 'No money'}
            user = self.users[id_]
            if service_id not in user.services:
                user.services[service_id] = copy.deepcopy(new_service)
            else:
                user.services[service_id].tokens += new_service.tokens
            #del user.buyable[service_id] 
            user.money -= new_service.price
            return {'ok': 'ok'}
        else:
            return {'error': 'No service'}

    def request_service(self, id_, service_id):
        services = self.users[id_].services
        if service_id in services:
            service = services[service_id]
            if service.tokens > 0:
                service.tokens -= 1
                return {'ok': 'ok',
                        'estimate': service.estimate}
            else:
                return {'error': 'Not enough'}
        else:
            return {"error": "No service"}

    def request_loan(self, id_, amount):
        user = self.users[id_]
        if amount < user.max_loan:
            user.loan += amount
            user.money += amount
            return {'ok': 'ok'}
        else:
            return {'error': "More than max loan"}

    def get_user_id(self, login, pass_):
        key = login + pass_
        if key not in self.users:
            raise InvalidId()
        return self.users.get(key)

    def store_in_history(self, id_, output, request):
        self.histories[id_].append({'input': request, 'output': output, 'event_id': b64encode(urandom(16)), 'stars': -1})
        return {'ok': 'ok'}

    def request_history(self, id_):
        return self.histories[id_]

    def rate_history_event(self, id_, event_id, stars):
        history = self.histories[id_]
        rated = False
        for i in xrange(0, len(history)):
            if history[i]['event_id'] == event_id:
                history[i]['stars'] = stars
                rated = True
        return {'ok': 'ok'} if rated else {'error': 'Nothing was rated'}

class InvalidId:
    pass
