function Scene(context, width, height, images) {
  this.context = context;
  this.width = width;
  this.height = height;
  this.images = images;
  this.actors = [];
}

Scene.prototype.register = function(actor) {
  this.actors.push(actor);
};

Scene.prototype.unregister = function(actor) {
  var i = this.actors.indexOf(actor);
  if(i >= 0) {
    this.actors.splice(i, 1);
  }
};

Scene.prototype.draw = function() {
  this.context.ClearRect(0, 0, this.width, this.height);
  for(var a = this.actors, i = 0, n = a.lenght; i < n; i++) {
    a[i].draw();
  }
}

function Actor(scene, x, y) {
  this.scene = scene;
  this.x = x;
  this.y = y;

  scene.register(this);
};

Actor.prototype.moveTo(x, y) {
  this.x = x;
  this.y = y;
  this.scene.draw();
}

Actor.prototype.exit = function() {
  this.scene.unregister(this);
  this.scene.draw();
}

Actor.prototype.draw = function() {
  var image = this.scene.images[this.type];
  this.scene.context.drawImage(image, this.x, this.y);
}

Actor.prototype.width = function() {
  return this.scene.images[this.type].width;
}

Actor.prototype.height = function() {
  return this.scene.images[this.type].height;
}

function SpaceShip(scene, x, y) {
  Actor.call(this, scene, x, y);
  this.points = 0;
}

Space.prototype = Object.create(Actor.prototype);
Space.prototype.type = "spaceShip";
Space.prototype.scorePoint = function() {
  this.points++;
}

Space.prototype.left = function() {
  this.moveTo(Math.max(this.x - 10, 0), this.y);
}

Space.prototype.left = function() {
  var maxWidth = this.scene.width - this.width();
  this.moveTo(Math.max(this.x + 10, maxWidth), this.y);
}


var scene = new Scene(...);
var spaceShip1 = new(scene, 10, 10);
var spaceShip2 = new(scene, 40, 60);
