#!/usr/bin/env python
# -*- coding: utf-8 -*-
from service import Service
from user import User


class DB:
    def __init__(self):
        self.services = [
            Service('Hydraulik', 5),
            Service('Ginekolog', 12),
            Service('Holowanie', 0),
        ]
        self.users = {
            'soperkrulDupa.8': User('soperkrul', 'Dupa.8', 'Jakub', 'Król', 1337, 666)
        }

    def get_services(self, id_):
        return self.services[0:2]

    def get_buyable_services(self, id_):
        return self.services[2]
                
    def get_user_info(self, id_):
        if id_ in self.users:
            return self.users[id_]

    def get_user_id(self, login, pass_):
        key = login + pass_
        return self.users.get(key)
