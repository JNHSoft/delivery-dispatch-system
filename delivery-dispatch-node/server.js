const express = require('express');
const app = express();
const winston = require('winston');
const winstonDaily = require('winston-daily-rotate-file');
const server = require('http').createServer(app);
const port = process.argv[2] || process.env.PORT || 3000;
const redis = require('redis');
const moment = require('moment');

process.on('uncaughtException', function (err) {
	//예상치 못한 예외 처리
	logger.error('uncaughtException 발생 : ' + err);
});

//log 경로
//TW
const logDir = '/root/logs/delivery-dispatch/oper/node/';
//HK
// const logDir = '/home/ddehk/logs/delivery-dispatch/oper/node/';
//TEST
//const logDir = '/home/ddetest/logs/deliver-dispatch/oper/node/'
//LOCAL
// const logDir = './log/';


//redis - HK, TW 공통
const subclient = redis.createClient(6379 , '192.168.100.81');
const pubclient = redis.createClient(6379 , '192.168.100.81');

//redis - LOCAL, TEST
//const subclient = redis.createClient(6379 , '127.0.0.1');
//const pubclient = redis.createClient(6379 , '127.0.0.1');

const myFormat = winston.format.printf(info => {
    return `${info.timestamp} ${info.level}: ${info.message}`;
});

function timeStampFormat() {
    return moment().format('YYYY-MM-DD HH:mm:ss.SSS ZZ');                            
};

let logger = winston.createLogger({
    format: winston.format.combine(
        winston.format.timestamp({format:'YYYY-MM-DD HH:mm:ss.SSS ZZ'}),
        myFormat
    ),
    transports: [
        new (winstonDaily)({
            name: 'info-file',
            filename: logDir + 'dde_node_application_%DATE%.log',
            datePattern: 'YYYYMMDD',
            colorize: false,
            maxSize: '2m',//200mb
            maxFiles: '5d',
            level: 'info',
            showLevel: true,
            json: false,
            timestamp: timeStampFormat
        }),
        new (winston.transports.Console)({
            name: 'debug-console',
            colorize: true,
            level: 'debug',
            showLevel: true,
            json: false,
            timestamp: timeStampFormat
        })
    ],
    exceptionHandlers: [
        new (winstonDaily)({
            name: 'exception-file',
            filename: logDir + 'dde_node_exception_%DATE%.log',
            datePattern: 'YYYYMMDD',
            colorize: false,
            maxSize: '2m',//200mb
            maxFiles: '5d',
            level: 'error',
            showLevel: true,
            json: false,
            timestamp: timeStampFormat
        }),
        new (winston.transports.Console)({
            name: 'exception-console',
            colorize: true,
            level: 'debug',
            showLevel: true,
            json: false,
            timestamp: timeStampFormat
        })
    ]
});

module.exports = logger;

const io = require('socket.io')(server, {
    path: '/socket.io', // 클라이언트 사이드 코드의 path와 동일해야 한다.
    transports: ['websocket'] // websocket만을 사용하도록 설정
});

subclient.subscribe('pubsub:web-notification');

subclient.on("error", function (err) {
    if(err){
        logger.err("err : "+ err);
    }
});
subclient.on("ready", function (ready) {
    if(ready){
        logger.err("ready : "+ ready);
    }
});
subclient.on("connect", function (connect) {
    if(connect){
        logger.err("connect : "+ connect);
    }
});

pubclient.on("error", function (err) {
    if(err){
        logger.err("connect : "+ err);
    }
});

subclient.on('message', (channel, message) => {
    try{
        if (JSON.parse(message).type != 'rider_location_updated') {
            logger.info(message);
            // socket.emit('message', message);
        }
    }
    catch(exception){
        logger.error(exception);
    }
});

io.sockets.on('connection', (socket) => {
    subclient.on('message', (channel, message) => {
        try{
            // logger.debug(message);
            socket.emit('message', message);
        }
        catch(exception){
            logger.error(exception);
        }
    }); 
    // socket.on('message', (message) => {
    //     logger.info(message);
    //     pubclient.publish("pubsub:web-notification", message);
    // });
});

server.listen(port, () => {
    logger.info(`Express listening on port ${port}`);
});