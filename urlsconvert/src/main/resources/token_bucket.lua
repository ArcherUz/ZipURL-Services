local bucketKEY = KEYS[1] -- IP as redis key
local tokensPerRequest = 1 -- each request consumes 1 token
local capacity = tonumber(ARGV[1]) -- total tokens
local refillTime = tonumber(ARGV[2]) -- token refill time in seconds
local refillAmount = tonumber(ARGV[3]) -- tokens to refill each time
local currentTime = tonumber(ARGV[4]) -- current time in seconds

-- get the bucket status, current token count, and last refill time
local bucket = redis.call('hmget', bucketKEY, 'tokens', 'lastRefill')

local lastTokens = tonumber(bucket[1])
local lastRefill = tonumber(bucket[2])

-- if the bucket is new or does not exist
if lastTokens == nil then
    lastTokens = capacity
    lastRefill = currentTime

    -- set initial tokens and last refill time
    redis.call('hset', bucketKEY, 'tokens', capacity, 'lastRefill', currentTime)
else
    -- calculate the time passed since the last refill
    local deltaTime = currentTime - lastRefill
    -- calculate tokens to be refilled based on time passed
    local refilledTokens = math.min(capacity, lastTokens + math.floor(deltaTime / refillTime) * refillAmount)

    -- update lastTokens with refilledTokens for further calculation
    lastTokens = refilledTokens
end

-- calculate new token count after a request consumes tokens
local newTokens = lastTokens - tokensPerRequest

if newTokens < 0 then
    -- if not enough tokens, return 0 to indicate the request is not allowed
    return 0
else
    -- if tokens are sufficient, update the bucket with new token count and current time as last refill time
    redis.call('hset', bucketKEY, 'tokens', newTokens, 'lastRefill', currentTime)
    -- return 1 to indicate the request is allowed
    return 1
end