# Exercice 1 & 2
curl -X GET $COUCH3
curl -X PUT $COUCH3/reiter_occitanie
curl -X GET $COUCH3/reiter_occitanie

curl -X POST $COUCH3/reiter_occitanie/_bulk_docs -d @aveyron.json -H "Content-Type: application/json"
curl -X POST $COUCH3/reiter_occitanie/_bulk_docs -d @gard.json -H "Content-Type: application/json"
curl -X POST $COUCH3/reiter_occitanie/_bulk_docs -d @hauteGaronne.json -H "Content-Type: application/json"
curl -X POST $COUCH3/reiter_occitanie/_bulk_docs -d @herault.json -H "Content-Type: application/json"
curl -X POST $COUCH3/reiter_occitanie/_bulk_docs -d @regions_partiel.json -H "Content-Type: application/json"

# Exercice3/
## 1/
curl -X GET $COUCH3
## 2/
curl -X GET $COUCH3/reiter_occitanie/
oui on peut le lire
## 3/
curl -X GET $COUCH3/reiter_occitanie/_all_docs
## 4/
curl -X GET $COUCH3/reiter_occitanie/34180

# Exercice 4

## 4.1.1
function(doc){
  if (doc.type == 'old_region') {
    emit(null, doc._id)
  }
}
## 4.1.2
function(doc){
    if(doc.type == 'commune'){
        emit(null, {
            "id": doc._id,
            "longitude": doc.longitude,
            "latitude": doc.latitude
        });
    }
}
## 4.1.3
## 4.1.4

## 4.2.1
## 4.2.2
## 4.2.3
## 4.2.4

## 4.3.1
## 4.3.2

# Exercice 5

## 5.1
## 5.2
## 5.3
## 5.4
## 5.5