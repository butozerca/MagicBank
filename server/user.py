from bankjson import JSONSerializable


class User(JSONSerializable):
    def __init__(self, login, pass_, name, last_name, email, money, max_loan, loan, tariff, services, buyable):
        self.id_ = login + pass_
        self.login = login
        self.pass_ = pass_
        self.name = name
        self.last_name = last_name
        self.email = email
        self.money = money
        self.max_loan = max_loan
        self.loan = loan
        self.tariff = tariff
        self.services = services
        self.buyable = buyable

    def to_json(self):
        return {
            'id': self.id_,
            'name': self.name,
            'last_name': self.last_name,
            'money': self.money,
            'max_loan': self.max_loan,
            'loan': self.loan,
            'tariff': self.tariff,
        }
