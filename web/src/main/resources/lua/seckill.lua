-- KEYS: stock key, buyers set, one-time submit token. ARGV[1]: user id
if redis.call('get', KEYS[3]) == false then return 3 end
local stock = tonumber(redis.call('get', KEYS[1]))
if stock == nil or stock <= 0 then return 1 end
if redis.call('sismember', KEYS[2], ARGV[1]) == 1 then return 2 end
redis.call('decr', KEYS[1])
redis.call('sadd', KEYS[2], ARGV[1])
redis.call('del', KEYS[3])
return 0
