# CC: Drones
Drones for CC: Tweaked

currently drones can:

- Go forward (using `drone.engineOn(true)`)
- Turn (using `drone.left(number)` and `drone.right(number)`)
- Go up and down (using `drone.up(number)` and `drone.down(number)`)
- Look forward and back (`drone.lookForward()` `drone.lookBack()`)
- Mine forward (`drone.breakForward()`)
- see the Drones rotation (`drone.rotation()`)
- see if the drone is colliding (`drone.isColliding()`)
- turn the drones hover function on (`drone.hoverOn(true)`)
- pick up blocks (and keep there data) below (`drone.pickupBlock()`)
- drop the block (`drone.dropBlock()`)
- pick up entities below (`drone.pickUpEntity()`)
- drop the entity below (`drone.dropEntity()`)

## Programming
using the Drone Workbench as a peripheral you can use two commands

(let `a` be the wrap e.g `a = peripheral.wrap("left")`)

- `a.api()` will reboot the computer after installing the drone api for ease of programming
- `a.export(path)` will take the lua program at `path` and send it to the first drone within 2 blocks of the workbench, this will then reboot the drone and start it executing

