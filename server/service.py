from bankjson import JSONSerializable


class Service(JSONSerializable):
    def __init__(self, id_, name, type_, tokens, estimate, description, phone, price):
        self.id_ = id_
        self.name = name
        self.type = type_,
        self.tokens = tokens
        self.estimate = estimate
        self.description = description
        self.price = price
        self.phone = phone

    def to_json(self):
        return {
            'id': self.id_,
            'name': self.name,
            'type': self.type,
            'tokens': self.tokens,
            'estimate': self.estimate,
            'description': self.description,
            'phone': self.phone,
        }
