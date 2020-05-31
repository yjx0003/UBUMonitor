import csv
import json
import requests


MOODLE_URL= "moodleURL"

PUBLIC_CONFIG = [{"index":0,"methodname":"tool_mobile_get_public_config","args":{}}]

def read_csv(path):
    universities=[]
    with open(path,encoding='utf-8') as csvfile:
        reader =csv.reader(csvfile)
        
        for row in reader:
            if row[1]:
                universities.append({"name":row[0],MOODLE_URL:row[1]})

    return universities

def fill_json(universities):
    for university in universities:
        try:
            r = requests.get(university[MOODLE_URL]+"/local/mobile/check.php?service=local_mobile")

            university["local_mobile_check"] = r.json() if r.status_code == 200 else "Error: "+str(r.status_code)
        except json.decoder.JSONDecodeError:
             university["local_mobile_check"] = "Error decoding json: " + r.text
        try:
            r = requests.post(university[MOODLE_URL]+"lib/ajax/service.php", json = PUBLIC_CONFIG)
            university["tool_mobile_get_public_config"] = r.json() if r.status_code == 200 else "Error: "+str(r.status_code)
        except json.decoder.JSONDecodeError:
            university["tool_mobile_get_public_config"] = "Error decoding json: " + r.text
       
        try:
           url =  university["tool_mobile_get_public_config"][0]["data"]["launchurl"] 
           r = requests.get(url, params= {"service":"local_mobile", "passport":1})
           university["launch"] = r.status_code
        except:
            university["launch"] = "no launch url"

if __name__ == "__main__":

    
  
    universities = read_csv("Moodles de Universidades spain.csv")
    fill_json(universities)
    with open('moodle_university.json','w', encoding='utf-8') as outfile:
   
        json.dump(universities, outfile, sort_keys=True,ensure_ascii=False, indent=4)