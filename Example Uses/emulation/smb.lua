--Simple Example that uses 3 Beam buttons
--Works with Super Mario Bros for the NES
--Button 1 will change certain enemies in the first slot to a Buzzy Beetle
--Button 2 will change certain enemies in the first slot to a Hammer Bro
--Button 0 will change certain enemies in the first slot to one of several enemies, randomly
--Works in BizHawk with necessary files
package.cpath = ";../?.dll;"
package.path = ";../socket/?.lua;"
socket = require('socket')
local con = socket.udp()
con:setsockname("*", 0)
con:setpeername("127.0.0.1", 7125)
con:settimeout(100);
-- find out which port we're using
local ip, port = con:getsockname()
-- Output the port we're using
print("Port: " .. port)
local inst
local buttid
local message = "0"

math.randomseed(os.time())
smb = {}
smb[1] = 0
smb[2] = 1
smb[3] = 5
smb[4] = 2

--Queue from http://stackoverflow.com/questions/37245889/lua-queue-implementation/37250097
--sorry, I'm not a huge fan of Lua, so I copied this to save time.
dataQ = {}
dataQ.first = 0
dataQ.last = -1
dataQ.data = {}

function insert(q, val)
  q.last = q.last + 1
  q.data[q.last] = val
end

function remove(q)
    rval = 0
    if (q.first > q.last) then 
      rval = -1
    else
      rval = q.data[q.first]
      q.data[q.first] = nil
      q.first = q.first + 1
    end
    return rval
end

while true do
    if emu.framecount() % 12 == 0 then
        if(emu.framecount() % 24 == 0) then
            --Send "0" if just requesting buttons,\
            --or a message if requesting buttons AND want to display the message
            con:send(message);
            message = "0"
        else
            inst = con:receive()
            if inst ~= "-1" then
                --a button was pushed.
                print("datagram: " .. inst)
                insert(dataQ,inst)
            end
        end
        if dataQ.first <= dataQ.last then
            --There's something in the queue
            if mainmemory.readbyte(15) ~= 0 then
                enone = mainmemory.readbyte(22)
                if enone == 6 or enone == 0 then
                    buttid = remove(dataQ)
                    --Something is being removed
                    print("--Executing--")
                    if buttid == "0" then
                        message = "A random enemy appears"
                        print("--Rand--")
                        randomNumber = 1
                        if enone == 6 then
                            randomNumber = math.random(4);
                        else
                            randomNumber = math.random(2);
                        end
                        mainmemory.writebyte(22,smb[randomNumber])
                    elseif buttid == "1" then
                        message = "A Buzzy Beetle appears"
                        mainmemory.writebyte(22,2)
                        print("--Beetle--")
                    elseif buttid == "2" then
                        message = "A Hammer Bro appears"
                        mainmemory.writebyte(22,5)
                        print("--Hammer--")
                    end
                    --print(mainmemory.readbyte(0x16).." - "..buttid)
                end
            end
        end
    end
	emu.frameadvance()
end	