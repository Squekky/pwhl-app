import requests

url = "https://api.sportradar.com/icehockey/trial/v2/en/seasons/sr%3Aseason%3A122567/summaries.json?api_key=wrogXgmj7FXIpTpumcjgQALmEK8Ay382hJz7wyhJ"

headers = {"accept": "application/json"}

response = requests.get(url, headers=headers)

print(response.text)