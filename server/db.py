#!/usr/bin/env python
# -*- coding: utf-8 -*-
from service import Service
from user import User


class DB:
    def __init__(self):
        self.users = {
            'soperkrulDupa.8': User('soperkrul', 'Dupa.8', 'Jakub', 'Król',
            1337, 666, 0, 'Economy Saver Negative',
            {
                '0': Service('0', 'Hydraulik', 5, 50, 'wklada rury', 200),
                '1': Service('1', 'Ginekolog', 12, 100, 'wyjmuje rury', 200),
                '2': Service('2', 'Holowanie', 0, 200, 'ciagnie rure', 300)
            },
            {
                '3': Service('3','Grzyby', 10, 10, 'grzyb', 800),
            })
        }

    def get_services(self, id_):
        return self.users[id_].services

    def get_buyable_services(self, id_):
        return self.users[id_].buyable
                
    def get_user_info(self, id_):
        if id_ in self.users:
            return self.users[id_]

    def buy_service(self, id_, service_id):
        services = self.users[id_].buyable
        if service_id in services:
            new_service = services[service_id]
            if service.price > self.users[id_].money:
                return {'error': 'No money'}
            user = self.users[id_]
            user.services[service_id] = new_service
            del user.buyable[service_id] 
            user.money -= new_service.price
        else:
            return {'error': 'No service'}

    def request_service(self, id_, service_id):
        services = self.users[id_].services
        if service_id in services:
            service = services[service_id]
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
