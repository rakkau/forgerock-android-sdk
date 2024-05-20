# README_RAKKAU

## Generating and logging a device profile
A function called `logDeviceInfo` has been [added](https://github.com/rakkau/forgerock-android-sdk/blob/rakkau/samples/auth/src/main/java/org/forgerock/auth/MainActivity.java#L136) to the original code, the propose of this function is to show an example of how to generate a `FRDeviceProfile` using the default `FrDeviceCollector` class. This will return a `JSONObject` containing the definition for the collected device, and it will be printed in the logcat. This function will run as soon as the application is launched.

```java
public void logDeviceInfo() {
  // * Device */
  FRDeviceCollector.DEFAULT.collect(getBaseContext(), new FRListener<JSONObject>() {

    @Override
    public void onSuccess(JSONObject result) {
      Logger.warn(TAG, "[Rakkau] logDeviceInfo: " + result);
    }

    @Override
    public void onException(Exception e) {

    }

  });
}
```

The output should be as the following

```
2024-05-20 13:30:06.381  2786-2786  ForgeRock               org.forgerock.auth                   W  [4.4.1-beta1] [MainActivity]: [Rakkau] logDeviceInfo: {"identifier":"2f2a480499e36147-a0aa798a21067dd7555808187649d5d7f41a48fd",
"version":"1.0","platform":
{"platform":"Android","version":34,"device":"emu64xa","deviceName":"sdk_gphone64_x86_64","model":"sdk_gphone64_x86_64","brand":"google","locale":"en_US","timeZone":"America\/Buenos_Aires","jailBreakScore":0},
"hardware":{"hardware":"ranchu","manufacturer":"Google","storage":5939,"memory":3921,"cpu":4,"display":{"width":1080,"height":2154,"orientation":1},"camera":{"numberOfCameras":1}},"browser":{"userAgent":"Mozilla\/5.0 (Linux; Android 14; sdk_gphone64_x86_64 Build\/UPB3.230519.006; wv) AppleWebKit\/537.36 (KHTML, like Gecko) Version\/4.0 Chrome\/113.0.5672.119 Mobile Safari\/537.36"},"bluetooth":{"supported":true},"network":{"connected":true},"telephony":
{"networkCountryIso":"us","carrierName":"T-Mobile"}}
```

Based on this example, every time the user launches the app, the log shows the SAME device ID (In the logs the `identifier` json attribute) . Since the app is the same and follows these rules: 
- https://backstage.forgerock.com/docs/sdks/latest/sdks/use-cases/device-profile/device-id.html#when_can_identifiers_change
