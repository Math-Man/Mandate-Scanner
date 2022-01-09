# Mandate Scanner

Web scraping project for detecting and comparing changes on Turkish government mandates website.

Uses MongoDB for local storage, has a rest end point for accessing the stored data

MandateManager class uses a scheduled task to scrape the target website, download the package and unzips it.
Comparisons between the mandate versions gets built on request from the rest end point.


## Usage
Just run it as if you would run any maven spring boot application. Entry point is on MandateScannerApplication class

Once started swagger can be accessed from url http://**url base**:**port**/swagger-ui/index.html#/mandate-controller
![Swagger image](/media/swagger.png)

## Storage

Under application properties, the storage database name can be changed.
By default, it creates a data repo and creates "mandates" and "comparisons" repositories under data repo.

         spring.data.mongodb.database = data
         data.mongo.documents.mandates = mandates
         data.mongo.documents.comparison = comparisons

#### Note
I was originally going to develop this further and put it on some api sharing website. 
Turns out there were complications that are outside my control so this project is going to live here now.