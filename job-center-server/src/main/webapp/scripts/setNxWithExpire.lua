local k,v,exp=KEYS[1],ARGV[1],ARGV[2];
local r=redis.call("SETNX",k,v);
if r == 1 then
    redis.call("EXPIRE",k,exp);
end
return r;