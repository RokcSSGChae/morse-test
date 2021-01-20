<template>
  <div>
    <div class="container">
      <div class="row">
        <div class="col-md-5">
          <div class="row">
            <div class="col-md-12">
              <a id="presenter" @click="test()" class="btn btn-success"><span
                  class="glyphicon glyphicon-play"></span> Presenter </a>
            </div>
            <div id="chattingContainer">
              <textarea id="chattingRecv" readonly></textarea>
              <input id="chattingSend" type="text">
              <a id="send" href="#" class="btn btn-primary"> <span
                  class="glyphicon glyphicon-forward"></span> Send
              </a>
            </div>
          </div>
        </div>
        <div class="col-md-7">
          <div id="videoBig">
            <video id="video" autoplay width="640px" height="480px" poster="../assets/spinner.gif"></video>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script
    src="bower_components/kurento-utils/js/kurento-utils.js"></script>
<script>
import axios from "axios"
import kurentoUtils from "kurento-utils"
import Vue from "vue"

var webRtcPeer;
var ws;
var video;


function showSpinner() {
  video = document.getElementById('video');
  video.poster = '../assets/transparent-1px.png';
  video.style.background = 'center transparent url("../assets/spinner.gif") no-repeat';
}

function onOfferPresenter(error, offerSdp) {
  if (error)
    return console.error('Error generating the offer');
  console.log('Invoking SDP offer callback function ' + offerSdp);
  console.log(roomIdx.value);
  var message = {
    id: 'presenter',
    sdpOffer: offerSdp,
    roomIdx: roomIdx.value,
    title: title.value,
    streamerId: streamerId.value
  }
  //console.log(message.sdpOffer)
  vm.sendToServer(message);
};

function presenterResponse(message) {
  if (message.response != 'accepted') {
    var errorMsg = message.message ? message.message : 'Unknow error';
    console.info('Call not accepted for the following reason: ' + errorMsg);
    dispose();
  } else {
    console.log(message.sdpAnswer)
    webRtcPeer.processAnswer(message.sdpAnswer, function (error) {
      if (error)
        return console.error(error);
    });
  }
}

function onIceCandidate(candidate) {
  var message = {
    id: 'onIceCandidate',
    candidate: JSON.stringify(candidate),
    roomIdx: roomIdx.value,
    title: title.value,
    streamerId: streamerId.value,
    isStreamer: true
  };
  vm.sendToServer(message);
};

var vm = new Vue({
  methods: {
    sendToServer(message) {
      var jsonMessage = JSON.stringify(message);
      console.log('Sending message: ' + jsonMessage);
      ws.send(jsonMessage);
    }
  }
})

export default {
  created() {
    console.log("try connection")
    ws = new WebSocket('wss://localhost:8443/call');
    ws.onopen = () => {
      console.log("connect");
    };

    ws.onmessage = (message) => {
      var parsedMessage = JSON.parse(message.data);
      console.info('Received message: ' + message.data);

      switch (parsedMessage.id) {
        case 'presenterResponse':
          presenterResponse(parsedMessage);
          break;
        case 'iceCandidate':
          webRtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
            if (error)
              return console.error('Error adding candidate: ' + error);
          });
          break;
        case 'stopCommunication':
          dispose();
          break;
        default:
          console.error('Unrecognized message', parsedMessage);
      }
    }
  },
  data() {
    return {
      roomIdx: this.$root.roomIdx,
      title: this.$root.title,
      streamerId: this.$root.streamerId,
    }
  },
  methods: {
    test() {
      this.presenter();
    },
    presenter() {
      showSpinner();
      var chattingRecv = document.getElementById('chattingRecv');
      var chattingSend = document.getElementById('chattingSend');

      var configuration = {
        'iceServers': [{
          'urls': 'turn:117.17.196.61:3478',
          'username': 'testuser',
          'credential': 'root',
        }]
      };

      function onMessage(event) {
        console.log("Received data " + event["data"]);
        chattingRecv.value = event["data"];
      }

      function onOpen(event) {
        var sendButton = document.getElementById('send');
        sendButton.addEventListener("click", function () {
          var data = chattingSend.value;
          console.log("Send button pressed. Sending data " + data);
          webRtcPeer.send(data);
          // http로 채팅 관리 서버에 채팅 보내는 거 추가해야 함
          chattingSend.value = "";
        });
        console.log("DataChannel open");
      }

      function onClosed(event) {
        console.log("DataChannel closed");
      }

      var options = {
        localVideo: video,
        dataChannels: true,
        dataChannelConfig: {
          // id: getChannelName(),
          onopen: onOpen,
          onClosed: onClosed,
          onmessage: onMessage
        },
        configuration: configuration,
        onicecandidate: onIceCandidate
      }

      webRtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendrecv(options,
          function (error) {
            if (error) {
              return console.log(error);
            }
            webRtcPeer.generateOffer(onOfferPresenter)
          });
    },
  }
}
</script>

<style>
html {
  position: relative;
  min-height: 100%;
}

body {
  padding-top: 40px;
}

video, #console {
  display: block;
  font-size: 14px;
  line-height: 1.42857143;
  color: #555;
  background-color: #fff;
  background-image: none;
  border: 1px solid #ccc;
  border-radius: 4px;
  -webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
  box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
  -webkit-transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
  transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

#console {
  overflow-y: auto;
  width: 100%;
  height: 175px;
}

#videoContainer {
  position: absolute;
  float: left;
}

#videoBig {
  width: 640px;
  height: 480px;
  top: 0;
  left: 0;
  z-index: 1;
}

div#videoSmall {
  width: 240px;
  height: 180px;
  padding: 0px;
  position: absolute;
  top: 15px;
  left: 400px;
  cursor: pointer;
  z-index: 10;
  padding: 0px;
}

div.dragged {
  cursor: all-scroll !important;
  border-color: blue !important;
  z-index: 10 !important;
}
</style>