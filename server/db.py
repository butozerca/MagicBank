#!/usr/bin/env python
# -*- coding: utf-8 -*-
from service import Service
from user import User


class DB:
    def __init__(self):
        self.users = {
            'soperkrulDupa.8': User('soperkrul', 'Dupa.8', 'Jakub', 'Król',
            1337, 666, 'Economy Saver Negative',
            [Service('Hydraulik', 5, 50, 'wklada rury'),
            Service('Ginekolog', 12, 100, 'wyjmuje rury'),
            Service('Holowanie', 0, 200, 'ciagnie rure')],
            [Service('Grzyby', 10, 10, 'grzyb'),
            ])
        }

    def get_services(self, id_):
        return self.users[id_].services

    def get_buyable_services(self, id_):
        return self.users[id_].buyable
                
    def get_user_info(self, id_):
        if id_ in self.users:
            return self.users[id_]

    def request_service(self, id_, service_index):
        services = self.users[id_].services
        if service_index < len(services):
            service = services[service_index]
            if service.tokens > 0:
                service.tokens -= 1
                return {'ok': 'ok'}
            else:
                return {'error': 'Not enough'}
            
        else:
            return {"error": "No service"}

    def get_user_id(self, login, pass_):
        key = login + pass_
        return self.users.get(key)
