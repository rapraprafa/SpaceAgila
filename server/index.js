var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];
var date = new Date()

server.listen(3000, function(){
    console.log("Server is now running...");
});

io.on('connection', function(socket){
    console.log("Player Connected!");
    socket.emit('socketID', { id: socket.id });
    socket.emit('getPlayers', players);
    socket.broadcast.emit('newPlayer', { id: socket.id });

    socket.on('playerMoved', function(data){
        data.id = socket.id;
        socket.broadcast.emit('playerMoved',data);


        for(var i = 0; i < players.length; i++){
            if(players[i].id == data.id){
                players[i].x = data.x;
                players[i].y = data.y;
            }
        }

    });


    socket.on('asteroidSpawned', function(data){
        socket.broadcast.emit('playerMoved', data);
        console.log(data.asteroid);

    });

    socket.on('disconnect', function(){
        console.log("Player Disconnected : " + Math.floor(Date.now() / 1000));
        socket.broadcast.emit('playerDisconnected', { id : socket.id});

        for(var i=0; i < players.length; i++){
            players.splice(i, 1);
        }
    });
    players.push(new player(socket.id, 0, 0));
});

function player(id, x, y){
    this.id = id;
    this.x = x;
    this.y = y;
}
