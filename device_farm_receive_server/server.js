const LISTEN_PORT = 9292;
const ARTIFACTS_SAVE_DIR = '../UoitDCLibraryBooking/build';
const DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT = 
    'http://api.uoitdclibrarybooking.objectivetruth.ca/circleci_build_webhook/upload_to_devicefarm';
const ANDROID_TEST_INSTRUMENTATION_APK_LOCATION =
    '../UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-androidTest-unaligned.apk';
const ANDROID_DEBUG_APK_LOCATION = 
    '../UoitDCLibraryBooking/build/outputs/apk/UoitDCLibraryBooking-debug-unaligned.apk';
const NGROK_TUNNEL_URL_LOCATION =
    '../ngrok_url.txt';
const EXIT_CODE_FILE_LOCATION =
    '../device_farm_receive_server_exit_code.txt';

var fs = require('fs');
console.log(`Writing 1 to ${EXIT_CODE_FILE_LOCATION}`);
fs.writeFileSync(EXIT_CODE_FILE_LOCATION, '1'); // Set failure incase we quit early

var express = require('express');
var url = require('url');
var app = express();
var bodyParser = require('body-parser');
var multer = require('multer');
var FormData = require('form-data');
var storage = multer.diskStorage({
    destination: function (req, file, callback) {
        callback(null, ARTIFACTS_SAVE_DIR);
    },
    filename: function (req, file, cb) {
        cb(null, file.fieldname + '.tgz');
    }
});
var upload = multer({storage: storage});
var NGROK_TUNNEL_URL_CALLBACK = fs.readFileSync(NGROK_TUNNEL_URL_LOCATION);




//==============MAIN=================

app.use(bodyParser.json()); // for parsing application/json
app.use(bodyParser.urlencoded({ extended: true })); // for parsing application/x-www-form-urlencoded

app.post('/reply', upload.single('artifacts'), function(req, res) {
    const DEVICE_FARM_RESULT_CODE_QUERY_STRING_FIELDNAME = 'resultcode';
    var resultCode = req.query[DEVICE_FARM_RESULT_CODE_QUERY_STRING_FIELDNAME];
    console.log(`Received response from device farm. Result Code: ${resultCode}, writing it to ${EXIT_CODE_FILE_LOCATION}`);
    fs.writeFileSync(EXIT_CODE_FILE_LOCATION, resultCode);
    process.exit(resultCode);
});

app.post('*', function(request, response) {
    response.send('Server is Up!');
});
app.get('*', function(request, response) {
    response.send('Server is Up!');
});
sendAPKsToDeviceFarmServerAndListenIfGoodResponse(app);





//============Utility Functions================

function sendAPKsToDeviceFarmServerAndListenIfGoodResponse(app) {
    console.log(`Sending the debug and instrumentation apks to the device farm located at ` + 
        `${DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT}. ` + 
        `Callback is ${NGROK_TUNNEL_URL_CALLBACK}`);

    var requestForCircleCIServer = new FormData();
    requestForCircleCIServer.append('instrumentation', fs.createReadStream(ANDROID_TEST_INSTRUMENTATION_APK_LOCATION));
    requestForCircleCIServer.append('debug', fs.createReadStream(ANDROID_DEBUG_APK_LOCATION));
    requestForCircleCIServer.submit({
        host: url.parse(DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT).host,
        path: url.parse(DEVICE_FARM_UPLOAD_APKS_FOR_TESTING_ENDPOINT).pathname +
            '?callback=' + encodeURIComponent(NGROK_TUNNEL_URL_CALLBACK)
    }, function(error, response){
        if(error || (response.statusCode < 200 || response.statusCode > 299)) {
            console.log(`Error Code: ${response.statusCode} when sending results to Device Farm Server`);
            console.log(error || response.statusMessage);
            console.log(`Writing 69 to ${EXIT_CODE_FILE_LOCATION}`);
            fs.writeFileSync(EXIT_CODE_FILE_LOCATION, '69');
            process.exit(0);
        }else {
            console.log(`Successfully transferred results to Device Farm Server, will wait for results...`);
            app.listen(LISTEN_PORT, function () {
                console.log(`Device Farm Receive Server listening on port ${LISTEN_PORT}!`);
            });
        }
    });
    
}

