# InfoCard-Android
InfoCard is a cross platform mobile SDK for credit card scanning. InfoCard can be used in M2C applications that needs credit card scanning. 

InfoCard uses the mobile phones camera to scan the credit card with its built-in OCR. An internet connection may be needed to verify your application token.

This is an Eclipse project of a sample application for demonstrating InfoCard SDK in Android devices. InfoCard SDK requires  Android SDK level 10 at least. 

In order to extract the scanned card information you need to supply a valid SDK token in MyScanActivity.java. 

Just place your SDK token in 113rd line

```
String sdkToken = "Please use sdk token sent to you here";
```

Please visit www.infodif.com/infocard and contact us for a trial SDK token 
