from bankjson import JSONSerializable


class Service(JSONSerializable):
    def __init__(self, name, tokens, estimate, description):
        self.name = name
        self.tokens = tokens
        self.estimate = estimate
        self.description = description

    def to_json(self):
        return {'name': self.name,
            'tokens': self.tokens,
            'estimate': self.estimate,
            'description': self.description,
            }
