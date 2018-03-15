const express = require('express');
const app = express();
const server = require('http').createServer(app);
const port = process.argv[2] || process.env.PORT || 3000;
const redis = require('redis');
const subclient = redis.createClient(6379 , 'nanum-taiwan-redis.e6yhbk.0001.apn2.cache.amazonaws.com');
const pubclient = redis.createClient(6379 , 'nanum-taiwan-redis.e6yhbk.0001.apn2.cache.amazonaws.com');
const io = require('socket.io')(server, {
    path: '/socket.io', // 클라이언트 사이드 코드의 path와 동일해야 한다.
    transports: ['websocket'] // websocket만을 사용하도록 설정
});
subclient.subscribe('pubsub:web-notification');

io.sockets.on('connection', (socket) => {
    socket.on('message', (message) => {
        console.log(message);
        pubclient.publish("pubsub:web-notification", message);
    }); 
    subclient.on('message', (channel, message) => {
        console.log(message);
        socket.emit('message', message);
    });
});

server.listen(port, () => {
    console.log(`Express listening on port ${port}`);
});