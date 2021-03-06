# Price-Offers

An Android app for a quick and straightforward lookup of various price offers for three Lithuanian supermarkets - Maxima, Iki, and Lidl.

The users can sort the offers in multiple categories, search for a specific offer, and select the offers they wish to save for future reference.

The app uses web scraping using jsoup to asynchronously scrape the latest price offers from the websites of the supermarkets. It stores the data with Room database and updates the RecyclerView using Android LiveData objects. The app uses Model-View-ViewModel system architecture.


### Made with:
* Android Jetpack
* Model-View-ViewModel architecture
* LiveData objects
* Room Database
* [jsoup web parser library](https://jsoup.org/)
* Android Fragments
* Android RecyclerViews

### Screenshots

  <p align="left">
    <img src="../screenshots/basic.png" alt="search" width="200" style="padding-left: 10px"/>
    <img src="../screenshots/search.png" alt="search" width="200" style="padding-left: 10px"/>
    <img src="../screenshots/cart.png" alt="search" width="200" style="padding-left: 10px"/>
  </p>
