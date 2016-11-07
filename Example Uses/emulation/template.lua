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
local inst
while true do
    --if emu.framecount() % 8 == 0 then
        con:send("0");
        inst = con:receive()
        if inst ~= "-1" then
            --a button was pushed.
            print("datagram: " .. inst)
        end
        emu.frameadvance()
    --end
end	