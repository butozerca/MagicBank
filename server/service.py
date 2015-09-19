from bankjson import JSONSerializable


class Service(JSONSerializable):
    def __init__(self, id_, name, tokens, estimate, description, price):
        self.id_ = id_
        self.name = name
        self.tokens = tokens
        self.estimate = estimate
        self.description = description
        self.price = price

    def to_json(self):
        return {
            'id': self.id_,
            'name': self.name,
            'tokens': self.tokens,
            'estimate': self.estimate,
            'description': self.description,
        }
