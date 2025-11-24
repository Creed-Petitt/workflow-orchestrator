FROM node:20-alpine

WORKDIR /app

COPY sdks/node ./workersdk
COPY examples/node/package.json .
COPY examples/node/tsconfig.json .
COPY examples/node/src ./src

RUN cd workersdk && npm install && npm run build
RUN npm install
RUN npm run build

CMD ["node", "dist/main.js"]
