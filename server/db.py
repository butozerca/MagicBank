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
                '0': Service('0', 'Hydraulik', 'house', 5, 50, 'wklada rury', '12-15-333-7', 200),
                '1': Service('1', 'Ginekolog', 'med', 12, 100, 'wyjmuje rury', '12-15-333-7', 200),
                '2': Service('2', 'Holowanie', 'car', 7, 200, 'ciagnie rure', '12-15-333-7', 300),
            },
            {
                '0': Service('0', 'Hydraulik', 'house', 5, 50, 'wklada rury', '12-15-333-7', 200),
                '1': Service('1', 'Ginekolog', 'med', 12, 100, 'wyjmuje rury', '12-15-333-7', 200),
                '2': Service('2', 'Holowanie', 'car', 7, 200, 'ciagnie rure', '12-15-333-7', 300),
                '3': Service('3','Grzyby', 'med', 10, 10, 'grzyb', '12-15-333-7', 800),
            }),
            'plizonasdf': User('plizon', 'asdf', 'Patryk', 'Lizoń', 'plizon@bank.pl',
            10000, 1000, 100, 'Uber Bank Pro Expert Assistance',
            {
                '0': Service('0', 'Hydraulik', 'house', 5, 60,
                    "Nasz bank dobrał elitarną ekipę doświadczonych fachowców "
                    "w dziedzinie utrzymywania właściwej wilgotności domóstw. ",
                    '606-612-309', 400),
                '1': Service('1', 'Holowanie', 'car', 10, 50,
                    "Szybkie i sprawne lawetowanie samochodów po ulicy i bezdrożach.",
                    '507-705-075', 450),
                '2': Service('2', 'Lekarz', 'med', 11, 75,
                    'Nerki tanio i szybko. Niezawodność kosztem usterek.',
                    '501-008-128', 303),
                '4': Service('4', 'Murarz', 'house', 2, 20,
                    'Murowana jakość. Solidny mur beton',
                    '321-345-112', 123),
                '5': Service('5', 'Helikopter', 'car', 1, 5,
                    'Zgubiłeś drogę? Zwiewasz przed CBA? A może lubisz placki? '
                    'Zamów helikopter już teraz. Z góry gwarantowana jakość.',
                    '505-105-205', 303),
                '6': Service('6', 'Karetka 24h', 'med', 1, 10,
                    'NFZ znowu cię zawiódł? Zamów karetkę, nie martw się o jutro.',
                    '112-999-137', 1000),
            },
            {
                '0': Service('0', 'Hydraulik', 'house', 5, 60,
                    "Nasz bank dobrał elitarną ekipę doświadczonych fachowców "
                    "w dziedzinie utrzymywania właściwej wilgotności domóstw. ",
                    '606-612-309', 400),
                '1': Service('1', 'Holowanie', 'car', 10, 50,
                    "Szybkie i sprawne lawetowanie samochodów po ulicy i bezdrożach.",
                    '507-705-075', 450),
                '2': Service('2', 'Lekarz', 'med', 11, 75,
                    'Nerki tanio i szybko. Niezawodność kosztem usterek.',
                    '501-008-128', 303),
                '4': Service('4', 'Murarz', 'house', 2, 20,
                    'Murowana jakość. Solidny mur beton',
                    '321-345-112', 123),
                '5': Service('5', 'Helikopter', 'car', 1, 5,
                    'Zgubiłeś drogę? Zwiewasz przed CBA? A może lubisz placki? '
                    'Zamów helikopter już teraz. Z góry gwarantowana jakość.',
                    '505-105-205', 303),
                '6': Service('6', 'Karetka 24h', 'med', 1, 10,
                    'NFZ znowu cię zawiódł? Zamów karetkę, nie martw się o jutro.',
                    '112-999-137', 1000),
                '3': Service('3', 'Elektryk', 'house', 7, 77,
                    'Porażająco sprawni panowie. Poeci w swej dziedzinie.',
                    '606-666-660', 655),
                '7': Service('7', 'Dowóz paliwa', 'car', 1, 25,
                    'Brakło paliwa, a stacji nie ma nigdzie w zasięgu wzroku?'
                    'To sprawa dla nich', '513-622-721', 240'),
            }),
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
