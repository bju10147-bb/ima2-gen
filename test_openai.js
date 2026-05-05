const https = require('https');
const data = JSON.stringify({
  model: "gpt-4o",
  input: [{ role: "user", content: [{ type: "input_text", text: "A cute cat" }] }],
  tools: [{ type: "image_generation", size: "1024x1024" }]
});

const req = https.request({
  hostname: 'api.openai.com',
  port: 443,
  path: '/v1/responses',
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer ' + (process.env.OPENAI_API_KEY || 'sk-fake')
  }
}, res => {
  let body = '';
  res.on('data', d => body += d);
  res.on('end', () => console.log(res.statusCode, body));
});
req.write(data);
req.end();
