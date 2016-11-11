--Template by Cory
--Get info from SocketGuy
--Works in BizHawk with necessary files
package.cpath = ";./?.dll;"
package.path = ";./socket/?.lua;"
socket = require('socket')
local con = socket.udp()
con:setsockname("*", 0)
con:setpeername("127.0.0.1", 7125)
-- find out which port we're using
local ip, port = con:getsockname()
-- Output the port we're using
print("Port: " .. port)
local message = "0"
local inst
while true do
    if emu.framecount() % 12 == 0 then
        if emu.framecount() % 24 == 0 then
            con:send(message);
            message = "0"
        else
            inst = con:receive()
            if inst ~= "-1" then
                --a button was pushed.
                print("datagram: " .. inst)
            end
        end
    end
    emu.frameadvance()
end	