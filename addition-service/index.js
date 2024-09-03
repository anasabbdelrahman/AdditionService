
import express, { Router } from 'express';

import * as crypto from 'crypto';

import kafka from "kafka-node";

const app = express();
const router = Router();
 
const port = process.env.PORT || 3000;

app.use(express.json());

router.use(function (req,res,next) {
    console.log(`${req.method} ${req.path}`);
    next();
});
    
router.post('/add', async function(req,res){
    try{
        var uuid = crypto.randomUUID();
    
        var event = {
            asyncId: uuid,
            numberOne: req.body.numberOne,
            numberTwo: req.body.numberTwo,
            result: req.body.numberOne + req.body.numberTwo
        };
        KafkaService.sendResult(uuid, event);
    
        res.json({
            asyncId: uuid
        });
    }catch(e){
        console.error('Error', e);
        res.status(400).json({
            error: 'Bad Request'
        });
    }
});

app.use(router);

app.listen(port, function () {
    console.log('Addition service listening on ' + port)
});

// KAFKA
const KafkaService = function(){
    console.log('Starting kafka service...');

    const client = new kafka.KafkaClient({ kafkaHost: "kafka:9092" });

    const producer = new kafka.HighLevelProducer(client);
    producer.on("ready", function() {
        console.log("Kafka Producer is connected and ready.");
    });

    producer.on("error", function(error) {
        console.error("Producer encountered an error:", error);
    });

    client.on("error", function(error) {
        console.error("Kafka Client encountered an error:", error);
    });

    return {
        sendResult: async function (asyncId, event){
            await new Promise(r => setTimeout(r, Math.random() * 5_000));

            console.log('Sending result:', asyncId, event);
            producer.send([
                {
                    topic: "addition-service.results",
                    messages: Buffer.from(JSON.stringify(event)),
                    attributes: 1
                }
            ], (e)=>{
                if(e) {
                    console.error('Unable to send event:', e);
                } else {
                    console.log('Event sent successfully!');
                }
            });
        }
    }
}();

