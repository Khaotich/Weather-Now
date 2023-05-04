import json
import sqlite3

file = open("city_list.json", 'r', encoding='utf-8')
data = json.load(file)
db = sqlite3.connect("cities.db")
db.cursor()

for i in range(len(data)):
    name = data[i]['name']
    country = data[i]['country']
    lon = data[i]['coord']['lon']
    lat = data[i]['coord']['lat']

    sql = f'INSERT INTO cities("name", "country", "lon", "lat") ' \
          f'VALUES ("{name}", "{country}", "{lon}", "{lat}");'

    db.execute(sql)
    db.commit()

    print(i)
