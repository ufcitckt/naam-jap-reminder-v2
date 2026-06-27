const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

let logContent = '';
function log(msg) {
    console.log(msg);
    logContent += msg + '\n';
}

log('=== JVM / COMPILER SEARCH ===');

const commands = ['java', 'javac', 'gradle', 'android', 'sdkmanager'];
for (const cmd of commands) {
    try {
        const out = execSync(`which ${cmd}`).toString().trim();
        log(`which ${cmd}: ${out}`);
        const ver = execSync(`${cmd} -version`).toString().trim();
        log(`${cmd} version info:\n${ver}`);
    } catch (e) {
        log(`${cmd} is NOT in PATH or failed to run: ${e.message}`);
    }
}

log('\n=== END JVM SEARCH ===');
fs.writeFileSync(path.join(__dirname, 'build_logs.txt'), logContent);
