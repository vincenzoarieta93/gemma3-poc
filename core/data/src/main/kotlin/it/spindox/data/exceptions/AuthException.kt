package it.spindox.data.exceptions

class MissingAccessTokenException : Exception("Please try again after sign in")

class UnauthorizedAccessException :
    Exception("Access denied. Please try again and grant the necessary permissions.")

class MissingUrlException : Exception("The selected model does not have a valid url")

class ModelNotFoundException() : Exception("Model not found")