from bankjson import JSONSerializable


class User(JSONSerializable):
    def __init__(self, login, pass_, name, last_name, money, max_loan):
        self.id_ = login + pass_
        self.login = login
        self.pass_ = pass_
        self.name = name
        self.last_name = last_name
        self.money = money
        self.max_loan = max_loan

    def to_json(self):
        return {
            'id_': self.id_,
            'name': self.name,
            'last_name': self.last_name,
            'money': self.money,
            'max_loan': self.max_loan,
        }
