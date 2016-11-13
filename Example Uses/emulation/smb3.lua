--SMB3 SockGuy Script
--Works with Super Mario Bros 3 for the NES
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
local inlevel = false
local message = "0"

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

--You can all this from the console to empty your inventory
function clearInventory()
    memory.writebyte(32128,0)
    memory.writebyte(32129,0)
    memory.writebyte(32130,0)
    memory.writebyte(32131,0)
    memory.writebyte(32132,0)
    memory.writebyte(32133,0)
    memory.writebyte(32134,0)
    memory.writebyte(32135,0)
    memory.writebyte(32136,0)
    memory.writebyte(32137,0)
    memory.writebyte(32138,0)
    memory.writebyte(32139,0)
    memory.writebyte(32140,0)
    memory.writebyte(32141,0)
    memory.writebyte(32142,0)
    memory.writebyte(32143,0)
    memory.writebyte(32144,0)
    memory.writebyte(32145,0)
    memory.writebyte(32146,0)
    memory.writebyte(32147,0)
    memory.writebyte(32148,0)
    memory.writebyte(32149,0)
    memory.writebyte(32150,0)
    memory.writebyte(32151,0)
    memory.writebyte(32152,0)
    memory.writebyte(32153,0)
    memory.writebyte(32154,0)
    memory.writebyte(32155,0)
    print("Inventory Emptied")
end
--alt command
function emptyInventory()
    clearInventory()
end

--Some memory is on SRAM
memory.usememorydomain("System Bus")

while true do
    if emu.framecount() % 12 == 0 then
        if emu.framecount() % 24 == 0 then
            --Send "0" if just requesting buttons,\
            --or a message if requesting buttons AND want to display the message
            con:send(message);
            message = "0"
            
            --Gives lives if running out
            --You can get rid of this if you want to be able to lose
            if mainmemory.readbyte(1846) <= 1 then
                mainmemory.writebyte(1846,2)
            end
        else
            inst = tonumber(con:receive())
            if inst ~= -1 then
                --a button was pushed.
                print("Button Pushed: " .. inst)
                insert(dataQ,inst)
            end
        end
    end
    --Process what's in the queue
    if emu.framecount() % 5 == 0 and dataQ.first <= dataQ.last then
        --There's something in the queue
        buttid = remove(dataQ)
        inlevel = (memory.readbyte(32252) == 54 and memory.readbyte(32253) == 39)
        if inlevel and buttid == 0 then
            --press P switch
            mainmemory.writebyte(1383,128)
            mainmemory.writebyte(1247,27)
            message = "P Switch Activated"
            print(message)
        elseif buttid > 0 and buttid <= 6 then
            if inlevel then
                mainmemory.writebyte(1400,buttid + 1)
                mainmemory.writebyte(1364,20)
                message = "Item Equipped"
            else
                mainmemory.writebyte(1862,buttid)
                message = "Item Will Be Equipped"
            end
            print(message)
        elseif buttid > 6 and buttid <= 19 then
            --
            message = "Item Added to Inventory"
            if memory.readbyte(32128) == 0 then
                memory.writebyte(32128,buttid - 6)
            elseif memory.readbyte(32129) == 0 then
                memory.writebyte(32129,buttid - 6)
            elseif memory.readbyte(32130) == 0 then
                memory.writebyte(32130,buttid - 6)
            elseif memory.readbyte(32131) == 0 then
                memory.writebyte(32131,buttid - 6)
            elseif memory.readbyte(32132) == 0 then
                memory.writebyte(32132,buttid - 6)
            elseif memory.readbyte(32133) == 0 then
                memory.writebyte(32133,buttid - 6)
            elseif memory.readbyte(32134) == 0 then
                memory.writebyte(32134,buttid - 6)
            elseif memory.readbyte(32135) == 0 then
                memory.writebyte(32135,buttid - 6)
            elseif memory.readbyte(32136) == 0 then
                memory.writebyte(32136,buttid - 6)
            elseif memory.readbyte(32137) == 0 then
                memory.writebyte(32137,buttid - 6)
            elseif memory.readbyte(32138) == 0 then
                memory.writebyte(32138,buttid - 6)
            elseif memory.readbyte(32139) == 0 then
                memory.writebyte(32139,buttid - 6)
            elseif memory.readbyte(32140) == 0 then
                memory.writebyte(32140,buttid - 6)
            elseif memory.readbyte(32141) == 0 then
                memory.writebyte(32141,buttid - 6)
            elseif memory.readbyte(32142) == 0 then
                memory.writebyte(32142,buttid - 6)
            elseif memory.readbyte(32143) == 0 then
                memory.writebyte(32143,buttid - 6)
            elseif memory.readbyte(32144) == 0 then
                memory.writebyte(32144,buttid - 6)
            elseif memory.readbyte(32145) == 0 then
                memory.writebyte(32145,buttid - 6)
            elseif memory.readbyte(32146) == 0 then
                memory.writebyte(32146,buttid - 6)
            elseif memory.readbyte(32147) == 0 then
                memory.writebyte(32147,buttid - 6)
            elseif memory.readbyte(32148) == 0 then
                memory.writebyte(32148,buttid - 6)
            elseif memory.readbyte(32149) == 0 then
                memory.writebyte(32149,buttid - 6)
            elseif memory.readbyte(32150) == 0 then
                memory.writebyte(32150,buttid - 6)
            elseif memory.readbyte(32151) == 0 then
                memory.writebyte(32151,buttid - 6)
            elseif memory.readbyte(32152) == 0 then
                memory.writebyte(32152,buttid - 6)
            elseif memory.readbyte(32153) == 0 then
                memory.writebyte(32153,buttid - 6)
            elseif memory.readbyte(32154) == 0 then
                memory.writebyte(32154,buttid - 6)
            elseif memory.readbyte(32155) == 0 then
                memory.writebyte(32155,buttid - 6)
            else
                message = "Inventory is Full"
            end
            print(message)
        else
            insert(dataQ,buttid)
        end
    end
	emu.frameadvance()
end	