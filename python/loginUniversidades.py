import csv
import json
import requests
import os

abspath = os.path.abspath(__file__)
dname = os.path.dirname(abspath)
os.chdir(dname)

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

            university["local_mobile_check"] = r.json()

        except:
            university["local_mobile_check"] = r.status_code
        try:
            r = requests.get(university[MOODLE_URL]+"/local/mobile/check.php?service=moodle_mobile_app")

            university["moodle_mobile_app_check"] = r.json()

        except:
            university["moodle_mobile_app_check"] = r.status_code
        try:
            r = requests.post(university[MOODLE_URL]+"lib/ajax/service.php", json = PUBLIC_CONFIG)
            university["tool_mobile_get_public_config"] = r.json()
            tool_mobile_get_public_config = r.json()

        except:
            tool_mobile_get_public_config = 404
        try:
           url =  tool_mobile_get_public_config[0]["data"]["launchurl"] 
           r = requests.get(url, params= {"service":"moodle_mobile_app", "passport":1})
           university["launch_moodle_mobile_app"] = r.status_code
        except:
            university["launch_moodle_mobile_app"] = 404
        try:
           url =  tool_mobile_get_public_config[0]["data"]["launchurl"] 
           r = requests.get(url, params= {"service":"local_mobile", "passport":1})
           university["launchurl_local_mobile"] = r.status_code
        except:
            university["launchurl_local_mobile"] = 404

        try:
           url =  university[MOODLE_URL]+"/local/mobile/launch.php"
           r = requests.get(url, params= {"service":"local_mobile", "passport":1})
           university["/local/mobile/launch.php"] = r.status_code
        except:
            university["/local/mobile/launch.php"] = 404

if __name__ == "__main__":

    
  
    universities = read_csv("Moodles de Universidades spain.csv")
    fill_json(universities)
    with open('moodle_university.json','w', encoding='utf-8') as outfile:
   
        json.dump(universities, outfile, sort_keys=True,ensure_ascii=False, indent=4)