const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3000;

app.use(express.json());

// API to load a file's content dynamically for the code explorer
app.get('/api/file', (req, res) => {
    const filePath = req.query.path;
    if (!filePath) return res.status(400).send('Path is required');
    
    // Safety check to keep path inside workspace
    const resolvedPath = path.resolve(path.join(__dirname, filePath));
    if (!resolvedPath.startsWith(__dirname)) {
        return res.status(403).send('Access denied');
    }

    try {
        if (fs.existsSync(resolvedPath)) {
            const content = fs.readFileSync(resolvedPath, 'utf-8');
            res.send(content);
        } else {
            res.status(404).send('File not found');
        }
    } catch (e) {
        res.status(500).send(e.message);
    }
});

// Download compiled APK endpoint
app.get('/download/app-debug.apk', (req, res) => {
    const apkPath = path.join(__dirname, 'app', 'build', 'outputs', 'apk', 'debug', 'app-debug.apk');
    if (fs.existsSync(apkPath)) {
        const stats = fs.statSync(apkPath);
        res.writeHead(200, {
            'Content-Type': 'application/vnd.android.package-archive',
            'Content-Disposition': 'attachment; filename="Naam_Jap_Reminder_v1.0.0.apk"',
            'Content-Length': stats.size
        });
        const readStream = fs.createReadStream(apkPath);
        readStream.pipe(res);
    } else {
        res.status(404).send('APK file not found. Please compile the app first.');
    }
});

// Serve the index.html portal
app.get('*', (req, res) => {
    res.send(getPortalHtml());
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`Android portal running on port ${PORT}`);
});

function getPortalHtml() {
    return `<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Naam Jap Reminder - Native Android Portal</title>
    <style>
        :root {
            --amber-dark: #b45309;
            --amber-medium: #d97706;
            --amber-light: #fef3c7;
            --saffron: #f59e0b;
            --stone-dark: #1c1917;
            --stone-medium: #44403c;
            --stone-light: #f5f5f4;
            --pure-white: #ffffff;
            --border-color: #e5e7eb;
        }

        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        body {
            background-color: #0f172a;
            color: #e2e8f0;
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            overflow-x: hidden;
        }

        header {
            background-color: #1e293b;
            border-bottom: 1px solid #334155;
            padding: 16px 24px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header h1 {
            font-size: 1.25rem;
            font-weight: 800;
            color: #f8fafc;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        header .badge {
            background-color: var(--amber-medium);
            color: white;
            font-size: 0.7rem;
            padding: 3px 8px;
            border-radius: 4px;
            font-weight: 900;
        }

        .export-btn {
            background-color: #10b981;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 700;
            font-size: 0.85rem;
            cursor: pointer;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .export-btn:hover {
            background-color: #059669;
            transform: translateY(-1px);
        }

        .download-apk-btn {
            background-color: #0284c7;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 8px;
            font-weight: 700;
            font-size: 0.85rem;
            cursor: pointer;
            transition: all 0.2s;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .download-apk-btn:hover {
            background-color: #0369a1;
            transform: translateY(-1px);
        }

        main {
            display: flex;
            flex: 1;
            padding: 24px;
            gap: 24px;
            max-width: 1600px;
            margin: 0 auto;
            width: 100%;
        }

        /* Emulator Style */
        .emulator-container {
            flex: 1;
            max-width: 420px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .phone-frame {
            width: 360px;
            height: 740px;
            background-color: #000;
            border-radius: 48px;
            border: 12px solid #2d3748;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.5);
            position: relative;
            overflow: hidden;
            display: flex;
            flex-direction: column;
        }

        .phone-notch {
            position: absolute;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 150px;
            height: 24px;
            background-color: #2d3748;
            border-bottom-left-radius: 16px;
            border-bottom-right-radius: 16px;
            z-index: 50;
        }

        .phone-screen {
            flex: 1;
            background-color: var(--stone-light);
            display: flex;
            flex-direction: column;
            padding-top: 24px;
            position: relative;
            color: var(--stone-dark);
        }

        /* Screen Header */
        .screen-header {
            background-color: var(--pure-white);
            padding: 14px 16px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
        }

        .screen-header-title {
            font-weight: 800;
            font-size: 0.95rem;
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .screen-header-subtitle {
            font-size: 0.65rem;
            color: var(--stone-medium);
            margin-top: 2px;
            font-weight: 500;
        }

        /* Screen Content */
        .screen-content {
            flex: 1;
            padding: 16px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        /* Screen Footer */
        .screen-nav {
            background-color: var(--pure-white);
            border-top: 1px solid var(--border-color);
            height: 56px;
            display: flex;
            justify-content: space-around;
            align-items: center;
        }

        .nav-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            color: var(--stone-medium);
            font-size: 0.65rem;
            font-weight: 600;
            cursor: pointer;
            gap: 2px;
            transition: all 0.2s;
        }

        .nav-item.active {
            color: var(--amber-medium);
        }

        .nav-item svg {
            width: 20px;
            height: 20px;
        }

        /* Mala Tab */
        .stat-card {
            background-color: var(--pure-white);
            border-radius: 16px;
            padding: 12px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            display: flex;
            justify-content: space-around;
            align-items: center;
            margin-bottom: 12px;
        }

        .stat-col {
            text-align: center;
        }

        .stat-val {
            font-size: 1.25rem;
            font-weight: 800;
            color: var(--amber-dark);
        }

        .stat-lbl {
            font-size: 0.6rem;
            color: var(--stone-medium);
            font-weight: 600;
            text-transform: uppercase;
        }

        .divider-v {
            width: 1px;
            height: 24px;
            background-color: #e2e8f0;
        }

        .mala-canvas-container {
            flex: 1;
            display: flex;
            justify-content: center;
            align-items: center;
            position: relative;
            margin: 16px 0;
        }

        .mala-bead-circle {
            width: 220px;
            height: 220px;
            position: relative;
            border-radius: 50%;
        }

        .mala-bead {
            position: absolute;
            width: 6px;
            height: 6px;
            border-radius: 50%;
            background-color: #e5e7eb;
            transform: translate(-50%, -50%);
            transition: background-color 0.1s;
        }

        .mala-bead.completed {
            background-color: var(--amber-medium);
        }

        .mala-bead.milestone {
            width: 8px;
            height: 8px;
        }

        .mala-center-pad {
            position: absolute;
            width: 120px;
            height: 120px;
            background: linear-gradient(135deg, var(--saffron), var(--amber-medium));
            border-radius: 50%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            color: white;
            cursor: pointer;
            box-shadow: 0 10px 15px -3px rgba(217, 119, 6, 0.3);
            border: 2px solid white;
            transition: transform 0.1s;
            user-select: none;
        }

        .mala-center-pad:active {
            transform: scale(0.92);
        }

        .mala-center-pad span {
            font-size: 1.1rem;
            font-weight: 800;
            line-height: 1.2;
        }

        .mala-center-pad .tap-label {
            font-size: 0.55rem;
            font-weight: 800;
            letter-spacing: 1px;
            margin-top: 4px;
            color: var(--amber-light);
        }

        .btn-test {
            background-color: var(--amber-medium);
            color: white;
            border: none;
            padding: 10px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 0.75rem;
            cursor: pointer;
            width: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 6px;
        }

        .btn-test:active {
            background-color: var(--amber-dark);
        }

        .btn-reset {
            color: var(--stone-medium);
            background: none;
            border: none;
            font-size: 0.65rem;
            font-weight: 700;
            cursor: pointer;
            margin-top: 8px;
            display: flex;
            align-items: center;
            gap: 4px;
        }

        /* Settings Tab */
        .settings-container {
            display: flex;
            flex-direction: column;
            gap: 12px;
            flex: 1;
            overflow-y: auto;
        }

        .settings-card {
            background-color: var(--pure-white);
            border-radius: 16px;
            padding: 12px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
        }

        .settings-card h3 {
            font-size: 0.8rem;
            font-weight: 800;
            color: var(--stone-dark);
            margin-bottom: 4px;
        }

        .settings-card p {
            font-size: 0.65rem;
            color: var(--stone-medium);
            margin-bottom: 8px;
        }

        .row-setting {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 6px 0;
        }

        .row-setting label {
            font-size: 0.75rem;
            font-weight: 600;
        }

        .select-mins {
            background-color: var(--amber-light);
            color: var(--amber-dark);
            font-weight: 800;
            padding: 4px 10px;
            border-radius: 6px;
            font-size: 0.7rem;
            border: none;
            cursor: pointer;
        }

        .switch-box {
            position: relative;
            display: inline-block;
            width: 36px;
            height: 20px;
        }

        .switch-box input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #cbd5e1;
            transition: .2s;
            border-radius: 20px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 14px; width: 14px;
            left: 3px; bottom: 3px;
            background-color: white;
            transition: .2s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: var(--amber-medium);
        }

        input:checked + .slider:before {
            transform: translateX(16px);
        }

        .radio-option {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 6px;
            border-radius: 8px;
            cursor: pointer;
            font-size: 0.75rem;
        }

        .radio-option.active {
            background-color: var(--amber-light);
            font-weight: 700;
            color: var(--amber-dark);
        }

        /* History Tab */
        .logs-list {
            display: flex;
            flex-direction: column;
            gap: 8px;
            flex: 1;
            overflow-y: auto;
        }

        .log-item {
            background-color: var(--pure-white);
            border-radius: 12px;
            padding: 10px;
            display: flex;
            align-items: center;
            gap: 10px;
            box-shadow: 0 1px 2px rgba(0,0,0,0.02);
            font-size: 0.75rem;
        }

        .log-item.mala {
            background-color: var(--amber-light);
            border: 1px solid rgba(217,119,6,0.2);
        }

        .log-time {
            font-size: 0.6rem;
            color: var(--stone-medium);
            font-family: monospace;
            margin-top: 2px;
        }

        /* Code Explorer Style */
        .code-explorer {
            flex: 2;
            background-color: #1e293b;
            border-radius: 16px;
            border: 1px solid #334155;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        .explorer-header {
            background-color: #0f172a;
            padding: 12px 18px;
            border-bottom: 1px solid #334155;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .explorer-title {
            font-size: 0.9rem;
            font-weight: 700;
            color: #94a3b8;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .explorer-body {
            display: flex;
            flex: 1;
            overflow: hidden;
        }

        .file-tree {
            width: 220px;
            background-color: #0f172a;
            border-right: 1px solid #334155;
            padding: 12px;
            overflow-y: auto;
        }

        .file-tree-title {
            font-size: 0.7rem;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: #64748b;
            margin-bottom: 12px;
            font-weight: 800;
        }

        .tree-item {
            font-size: 0.75rem;
            padding: 6px 8px;
            border-radius: 6px;
            cursor: pointer;
            color: #94a3b8;
            display: flex;
            align-items: center;
            gap: 6px;
            transition: all 0.2s;
            margin-bottom: 2px;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .tree-item:hover {
            background-color: #1e293b;
            color: #f8fafc;
        }

        .tree-item.active {
            background-color: var(--amber-medium);
            color: white;
            font-weight: 700;
        }

        .code-viewer {
            flex: 1;
            padding: 16px;
            overflow: auto;
            background-color: #0b0f19;
            position: relative;
        }

        .code-viewer pre {
            font-family: 'Fira Code', 'JetBrains Mono', Courier, monospace;
            font-size: 0.8rem;
            line-height: 1.5;
            color: #cbd5e1;
        }

        /* Guide */
        .guide-container {
            display: flex;
            flex-direction: column;
            gap: 12px;
            overflow-y: auto;
        }

        .guide-card {
            background-color: var(--pure-white);
            border-radius: 16px;
            padding: 12px;
            font-size: 0.75rem;
            line-height: 1.4;
        }

        .guide-card h3 {
            font-size: 0.8rem;
            font-weight: 800;
            color: var(--stone-dark);
            margin-bottom: 4px;
        }

        .guide-card.tip {
            background-color: var(--amber-light);
            border: 1px solid rgba(217,119,6,0.15);
        }

        .guide-card.tip h3 {
            color: var(--amber-dark);
        }

        /* Notification Toast Simulation */
        .toast-notification {
            position: absolute;
            top: 40px;
            left: 12px;
            right: 12px;
            background-color: rgba(255, 255, 255, 0.95);
            border-left: 4px solid var(--amber-medium);
            border-radius: 8px;
            padding: 10px 12px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 100;
            display: flex;
            align-items: center;
            gap: 10px;
            transform: translateY(-100px);
            opacity: 0;
            transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }

        .toast-notification.show {
            transform: translateY(0);
            opacity: 1;
        }

        .toast-title {
            font-weight: 800;
            font-size: 0.75rem;
            color: var(--stone-dark);
        }

        .toast-text {
            font-size: 0.65rem;
            color: var(--stone-medium);
            margin-top: 1px;
        }
    </style>
</head>
<body>

    <header>
        <h1>🌸 Naam Jap Reminder <span class="badge">NATIVE KOTLIN / COMPOSE APP</span></h1>
        <div style="display: flex; gap: 10px;">
            <button class="download-apk-btn" onclick="window.open('/download/app-debug.apk', '_blank')">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                Download Compiled APK
            </button>
            <button class="export-btn" onclick="exportProject()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
                Export Project ZIP
            </button>
        </div>
    </header>

    <main>
        <!-- EMULATOR COLUMN -->
        <div class="emulator-container">
            <div class="phone-frame">
                <div class="phone-notch"></div>
                
                <div class="phone-screen">
                    <!-- Notification Toast -->
                    <div id="toast" class="toast-notification">
                        <span style="font-size: 1.2rem;">🌸</span>
                        <div>
                            <div class="toast-title">Naam Jap Reminder</div>
                            <div class="toast-text">राधा वल्लभ श्री हरिवंश | Radha Vallabh</div>
                        </div>
                    </div>

                    <!-- Screen Header -->
                    <div class="screen-header">
                        <div class="screen-header-title">
                            Naam Jap Reminder
                            <span style="background-color: var(--amber-medium); color: white; font-size: 0.5rem; padding: 2px 4px; border-radius: 2px; font-weight: 800;">OFFLINE</span>
                        </div>
                        <div class="screen-header-subtitle">राधा वल्लभ श्री हरिवंश | Radhavallabh</div>
                    </div>

                    <!-- Screen Content -->
                    <div class="screen-content">
                        <!-- MALA SCREEN -->
                        <div id="tab-content-mala" class="tab-pane">
                            <div class="stat-card">
                                <div class="stat-col">
                                    <div id="beads-val" class="stat-val">0 / 108</div>
                                    <div class="stat-lbl">Beads</div>
                                </div>
                                <div class="divider-v"></div>
                                <div class="stat-col">
                                    <div id="rounds-val" class="stat-val">0</div>
                                    <div class="stat-lbl">Rounds</div>
                                </div>
                            </div>

                            <div class="mala-canvas-container">
                                <div id="mala-circle" class="mala-bead-circle"></div>
                                <div class="mala-center-pad" onclick="chantBead()">
                                    <span>राधा वल्लभ</span>
                                    <span>श्री हरिवंश</span>
                                    <div class="tap-label">TAP TO CHANT</div>
                                </div>
                            </div>

                            <div style="display: flex; flex-direction: column; gap: 8px;">
                                <button class="btn-test" onclick="fireTestReminder()">
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="margin-right: 2px;"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                                    Test Reminder Firing
                                </button>
                                <center>
                                    <button class="btn-reset" onclick="resetMalaCount()">
                                        <svg width="10" height="10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"><path d="M21.5 2v6h-6M21.34 15.57a10 10 0 1 1-.57-8.38l5.67-5.67"></path></svg>
                                        Reset Mala Chanting Count
                                    </button>
                                </center>
                            </div>
                        </div>

                        <!-- SETTINGS SCREEN -->
                        <div id="tab-content-settings" class="tab-pane" style="display: none;">
                            <div class="settings-container">
                                <div class="settings-card">
                                    <h3>Reminder Interval</h3>
                                    <p>Configure repeating cycle minutes for reminders.</p>
                                    <div class="row-setting">
                                        <span style="font-size: 0.75rem; font-weight: 500;">Interval Period:</span>
                                        <select id="interval-select" class="select-mins">
                                            <option value="15">15 Minutes</option>
                                            <option value="30">30 Minutes</option>
                                            <option value="45">45 Minutes</option>
                                            <option value="60">60 Minutes</option>
                                        </select>
                                    </div>
                                </div>

                                <div class="settings-card">
                                    <h3>Reminder Actions</h3>
                                    <div class="row-setting">
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700;">Show Notifications</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium);">Receive status popups</div>
                                        </div>
                                        <label class="switch-box">
                                            <input type="checkbox" id="notify-check" checked>
                                            <span class="slider"></span>
                                        </label>
                                    </div>
                                    <div class="row-setting" style="margin-top: 6px; border-top: 1px solid var(--stone-light); padding-top: 8px;">
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700;">Play Sound Alerts</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium);">Hear meditative audio</div>
                                        </div>
                                        <label class="switch-box">
                                            <input type="checkbox" id="sound-check" checked onchange="toggleSoundView()">
                                        </label>
                                    </div>
                                </div>

                                <div id="sound-options-card" class="settings-card">
                                    <h3>Sound / Chant Options</h3>
                                    <div id="sound-gong" class="radio-option active" onclick="setSoundType('gong')">
                                        <input type="radio" checked name="soundtype"> 🌸 Temple Gong
                                    </div>
                                    <div id="sound-bell" class="radio-option" onclick="setSoundType('bell')" style="margin-top: 4px;">
                                        <input type="radio" name="soundtype"> 🔔 Meditative Bell
                                    </div>
                                    <div id="sound-tts" class="radio-option" onclick="setSoundType('tts')" style="margin-top: 4px;">
                                        <input type="radio" name="soundtype"> 🗣️ Voice TTS
                                    </div>
                                </div>

                                <div class="settings-card">
                                    <div class="row-setting">
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700;">Quiet Hours Pause</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium);">Pause alerts during sleep</div>
                                        </div>
                                        <label class="switch-box">
                                            <input type="checkbox" id="quiet-check" onchange="toggleQuietView()">
                                            <span class="slider"></span>
                                        </label>
                                    </div>
                                    <div id="quiet-times" style="display: none; margin-top: 10px; border-top: 1px solid var(--stone-light); padding-top: 10px;">
                                        <div style="display: flex; gap: 8px;">
                                            <div style="flex: 1;">
                                                <label style="font-size: 0.6rem; font-weight: 700; color: var(--stone-medium);">Starts At</label>
                                                <input type="text" value="22:00" style="width: 100%; padding: 4px; border: 1px solid var(--border-color); border-radius: 4px; font-size: 0.7rem;">
                                            </div>
                                            <div style="flex: 1;">
                                                <label style="font-size: 0.6rem; font-weight: 700; color: var(--stone-medium);">Ends At</label>
                                                <input type="text" value="06:00" style="width: 100%; padding: 4px; border: 1px solid var(--border-color); border-radius: 4px; font-size: 0.7rem;">
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="settings-card" style="margin-top: 12px; display: flex; flex-direction: column; gap: 12px;">
                                    <h3 style="font-size: 0.8rem; font-weight: 800; color: var(--stone-dark); border-bottom: 1px solid var(--border-color); padding-bottom: 6px; margin-bottom: 4px;">Support & Feedback</h3>
                                    
                                    <!-- Send Feedback -->
                                    <div class="support-item" onclick="window.open('https://instagram.com/meetdudhatt', '_blank')" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">📷</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Send Feedback</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">Report bugs, request new features, share suggestions, or contact the developer.</div>
                                        </div>
                                    </div>

                                    <div style="height: 1px; background-color: var(--border-color);"></div>

                                    <!-- Business Inquiry -->
                                    <div class="support-item" onclick="window.location.href='mailto:meetdudhatt@gmail.com?subject=Business%20Inquiry%20-%20Naam%20Jap%20Reminder'" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">📧</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Business Inquiry</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">For business partnerships, collaborations, sponsorships, or professional inquiries.</div>
                                        </div>
                                    </div>

                                    <div style="height: 1px; background-color: var(--border-color);"></div>

                                    <!-- Report a Bug -->
                                    <div class="support-item" onclick="window.open('https://instagram.com/meetdudhatt', '_blank')" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">🐞</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Report a Bug</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">Open Instagram to report bugs directly: https://instagram.com/meetdudhatt</div>
                                        </div>
                                    </div>

                                    <div style="height: 1px; background-color: var(--border-color);"></div>

                                    <!-- Request a Feature -->
                                    <div class="support-item" onclick="window.open('https://instagram.com/meetdudhatt', '_blank')" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">💡</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Request a Feature</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">Open Instagram to request new features: https://instagram.com/meetdudhatt</div>
                                        </div>
                                    </div>

                                    <!-- Rate This App (Hidden until published) -->
                                    <!--
                                    <div style="height: 1px; background-color: var(--border-color);"></div>
                                    <div class="support-item" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">⭐</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Rate This App</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">Rate us on Google Play Store.</div>
                                        </div>
                                    </div>
                                    -->

                                    <div style="height: 1px; background-color: var(--border-color);"></div>

                                    <!-- Share App -->
                                    <div class="support-item" onclick="simulateShareSheet()" style="cursor: pointer; display: flex; align-items: flex-start; gap: 8px;">
                                        <span style="font-size: 1rem; margin-top: 2px;">📤</span>
                                        <div>
                                            <div style="font-size: 0.75rem; font-weight: 700; color: var(--stone-dark);">Share App</div>
                                            <div style="font-size: 0.6rem; color: var(--stone-medium); line-height: 1.3;">Share this app with friends and family using Android Share Sheet.</div>
                                        </div>
                                    </div>
                                </div>

                                <!-- About Footer -->
                                <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 16px 0; gap: 4px; color: var(--stone-medium); font-size: 0.65rem; text-align: center;">
                                    <div style="font-weight: 600; color: var(--stone-dark);">Made with ❤️ by Meet Dudhat</div>
                                    <div>Version 1.0.0</div>
                                    <div>© 2026 Meet Dudhat</div>
                                </div>
                            </div>
                        </div>

                        <!-- HISTORY SCREEN -->
                        <div id="tab-content-history" class="tab-pane" style="display: none; height: 100%; flex-direction: column;">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
                                <div style="font-size: 0.8rem; font-weight: 800;">Chanting Logs</div>
                                <button style="background: none; border: none; color: red; font-size: 0.65rem; font-weight: 700; cursor: pointer;" onclick="clearHistory()">Clear All</button>
                            </div>
                            <div id="logs-list-container" class="logs-list">
                                <!-- Logs will stream here -->
                            </div>
                        </div>

                        <!-- GUIDE SCREEN -->
                        <div id="tab-content-guide" class="tab-pane" style="display: none;">
                            <div class="guide-container">
                                <div class="guide-card">
                                    <h3>Naam Jap Meditative Guide</h3>
                                    <p style="margin-top: 4px; color: var(--stone-medium);">Naam Jap (Repetition of the Divine Name) is an ancient, potent spiritual practice. Chanting 'राधा वल्लभ श्री हरिवंश' connects the mind directly to pure transcendental divine love, peace, and spiritual focus.</p>
                                </div>
                                <div class="guide-card">
                                    <h3>Fully Offline-First Engine</h3>
                                    <p style="margin-top: 4px; color: var(--stone-medium);">Our app requires zero servers or data transfers. All settings, history logs, and chants are stored locally on your device inside Room and Jetpack DataStore databases.</p>
                                </div>
                                <div class="guide-card tip">
                                    <h3>Quick Chanting Tip</h3>
                                    <p style="margin-top: 4px; font-style: italic; color: var(--stone-dark);">Whenever you receive a periodic alert, pause for 10 seconds, take a deep breath, repeat 'राधा वल्लभ श्री हरिवंश' with devotion, and tap the center Mala bead pad. It builds steady focus!</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Navigation Footer -->
                    <div class="screen-nav">
                        <div id="nav-mala" class="nav-item active" onclick="switchTab(0)">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="m12 3-1.912 5.886L5 10.8l4.912 1.914L12 18.6l1.912-5.886L19 10.8l-4.912-1.914Z"></path><path d="M12 18v3"></path></svg>
                            Mala
                        </div>
                        <div id="nav-settings" class="nav-item" onclick="switchTab(1)">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.1a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path><circle cx="12" cy="12" r="3"></circle></svg>
                            Settings
                        </div>
                        <div id="nav-history" class="nav-item" onclick="switchTab(2)">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 8v4l3 3"></path><circle cx="12" cy="12" r="10"></circle></svg>
                            History
                        </div>
                        <div id="nav-guide" class="nav-item" onclick="switchTab(3)">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"></path><line x1="12" y1="17" x2="12.01" y2="17"></line></svg>
                            Guide
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- SOURCE CODE EXPLORER COLUMN -->
        <div class="code-explorer">
            <div class="explorer-header">
                <div class="explorer-title">
                    <span style="font-size: 1.1rem;">📂</span> Native Android Kotlin Codebase Explorer
                </div>
                <div style="font-size: 0.75rem; color: #64748b; font-weight: 500;">
                    Click a file to read production sources
                </div>
            </div>

            <div class="explorer-body">
                <div class="file-tree">
                    <div class="file-tree-title">Kotlin Sources</div>
                    <div class="tree-item active" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/MainActivity.kt', this)">MainActivity.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/ReminderWorker.kt', this)">ReminderWorker.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/ReminderViewModel.kt', this)">ReminderViewModel.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/DataStoreManager.kt', this)">DataStoreManager.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/MalaDatabase.kt', this)">MalaDatabase.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/ChantLog.kt', this)">ChantLog.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/ChantLogDao.kt', this)">ChantLogDao.kt</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/kotlin/com/naamjap/reminder/BootReceiver.kt', this)">BootReceiver.kt</div>

                    <div class="file-tree-title" style="margin-top: 14px;">Build & Configs</div>
                    <div class="tree-item" onclick="loadFile('app/src/main/AndroidManifest.xml', this)">AndroidManifest.xml</div>
                    <div class="tree-item" onclick="loadFile('app/build.gradle.kts', this)">app/build.gradle.kts</div>
                    <div class="tree-item" onclick="loadFile('build.gradle.kts', this)">build.gradle.kts</div>
                    <div class="tree-item" onclick="loadFile('settings.gradle.kts', this)">settings.gradle.kts</div>
                </div>

                <div class="code-viewer">
                    <pre><code id="code-box">Loading production file...</code></pre>
                </div>
            </div>
        </div>
    </main>

    <script>
        // Tab switching
        function switchTab(idx) {
            const tabs = ['mala', 'settings', 'history', 'guide'];
            tabs.forEach((tab, i) => {
                const element = document.getElementById('tab-content-' + tab);
                const nav = document.getElementById('nav-' + tab);
                if (i === idx) {
                    element.style.display = (tab === 'history') ? 'flex' : 'block';
                    nav.classList.add('active');
                } else {
                    element.style.display = 'none';
                    nav.classList.remove('active');
                }
            });
        }

        // Web Audio Gong & Bell Synthesizer
        let audioCtx = null;
        function playChime(type) {
            try {
                if (!audioCtx) audioCtx = new (window.AudioContext || window.webkitAudioContext)();
                if (audioCtx.state === 'suspended') audioCtx.resume();

                const osc = audioCtx.createOscillator();
                const gain = audioCtx.createGain();
                osc.connect(gain);
                gain.connect(audioCtx.destination);

                if (type === 'bell') {
                    // Ringing Bell (high pitch, slow release)
                    osc.type = 'sine';
                    osc.frequency.setValueAtTime(880, audioCtx.currentTime); // A5
                    gain.gain.setValueAtTime(0.5, audioCtx.currentTime);
                    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 1.5);
                    osc.start();
                    osc.stop(audioCtx.currentTime + 1.5);
                } else if (type === 'gong') {
                    // Deep Gong (lower pitch, warm feel)
                    osc.type = 'triangle';
                    osc.frequency.setValueAtTime(220, audioCtx.currentTime); // A3
                    gain.gain.setValueAtTime(0.6, audioCtx.currentTime);
                    gain.gain.exponentialRampToValueAtTime(0.001, audioCtx.currentTime + 2.5);
                    osc.start();
                    osc.stop(audioCtx.currentTime + 2.5);
                } else if (type === 'tts') {
                    // Speech voice synth
                    const utterance = new SpeechSynthesisUtterance("Radha Vallabh Shri Harivansh");
                    utterance.lang = "en-US";
                    utterance.rate = 0.85;
                    window.speechSynthesis.speak(utterance);
                }
            } catch (e) {
                console.error(e);
            }
        }

        // Mala counter state
        let completedBeads = 0;
        let totalRounds = 0;
        const totalBeads = 108;
        const logs = [];

        // Generate Mala Dots
        function generateMalaDots() {
            const container = document.getElementById('mala-circle');
            container.innerHTML = '';
            const radius = 95; // px
            const center = 110; // px

            for (let i = 0; i < totalBeads; i++) {
                const angle = (i * 360 / totalBeads) * (Math.PI / 180);
                const x = center + radius * Math.cos(angle);
                const y = center + radius * Math.sin(angle);

                const bead = document.createElement('div');
                bead.id = 'bead-' + i;
                bead.className = 'mala-bead';
                if (i % 9 === 0) bead.classList.add('milestone');
                bead.style.left = x + 'px';
                bead.style.top = y + 'px';

                container.appendChild(bead);
            }
        }

        function updateMalaUI() {
            document.getElementById('beads-val').innerText = completedBeads + ' / ' + totalBeads;
            document.getElementById('rounds-val').innerText = totalRounds;

            for (let i = 0; i < totalBeads; i++) {
                const b = document.getElementById('bead-' + i);
                if (b) {
                    if (i < completedBeads) {
                        b.classList.add('completed');
                    } else {
                        b.classList.remove('completed');
                    }
                }
            }
        }

        function chantBead() {
            // Haptic/Sound
            if (navigator.vibrate) navigator.vibrate(40);
            
            const isSound = document.getElementById('sound-check').checked;
            const soundType = getSelectedSoundType();
            if (isSound) {
                playChime(soundType);
            }

            completedBeads++;
            if (completedBeads >= totalBeads) {
                completedBeads = 0;
                totalRounds++;
                addLog('Completed 1 full Mala round (' + totalRounds + ' total)');
            } else if (completedBeads % 10 === 0) {
                addLog('Chanted ' + completedBeads + ' beads of current round');
            }

            updateMalaUI();
        }

        function resetMalaCount() {
            completedBeads = 0;
            totalRounds = 0;
            updateMalaUI();
            addLog('Mala progress manually reset');
        }

        function getSelectedSoundType() {
            if (document.getElementById('sound-gong').classList.contains('active')) return 'gong';
            if (document.getElementById('sound-bell').classList.contains('active')) return 'bell';
            return 'tts';
        }

        function setSoundType(type) {
            ['gong', 'bell', 'tts'].forEach(opt => {
                const el = document.getElementById('sound-' + opt);
                if (opt === type) {
                    el.classList.add('active');
                    el.querySelector('input').checked = true;
                } else {
                    el.classList.remove('active');
                }
            });
            playChime(type);
        }

        function toggleSoundView() {
            const isSound = document.getElementById('sound-check').checked;
            document.getElementById('sound-options-card').style.opacity = isSound ? '1' : '0.4';
            document.getElementById('sound-options-card').style.pointerEvents = isSound ? 'auto' : 'none';
        }

        function toggleQuietView() {
            const isQuiet = document.getElementById('quiet-check').checked;
            document.getElementById('quiet-times').style.display = isQuiet ? 'block' : 'none';
        }

        // Notification Simulation
        function fireTestReminder() {
            const toast = document.getElementById('toast');
            toast.classList.add('show');
            playChime('bell');
            addLog('Alert Triggered (MEDITATIVE BELL)');

            setTimeout(() => {
                toast.classList.remove('show');
            }, 4000);
        }

        // Logs Manager
        function addLog(detail) {
            const logObj = {
                id: Date.now(),
                timestamp: Date.now(),
                type: detail.includes('Chanted') || detail.includes('Mala') ? 'MALA' : 'REMINDER',
                detail: detail
            };
            logs.unshift(logObj);
            renderLogs();
        }

        function renderLogs() {
            const container = document.getElementById('logs-list-container');
            container.innerHTML = '';

            logs.forEach(log => {
                const item = document.createElement('div');
                item.className = 'log-item' + (log.type === 'MALA' ? ' mala' : '');
                
                const timeStr = new Date(log.timestamp).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit', second:'2-digit'});
                
                item.innerHTML = \`
                    <span style="font-size: 1rem;">\${log.type === 'MALA' ? '🌸' : '🔔'}</span>
                    <div>
                        <div style="font-weight: bold; color: var(--stone-dark);">\${log.detail}</div>
                        <div class="log-time">\${timeStr}</div>
                    </div>
                \`;
                container.appendChild(item);
            });
        }

        function clearHistory() {
            logs.length = 0;
            renderLogs();
        }

        // Load source file into code explorer
        function loadFile(filePath, element) {
            // Update active state in tree
            document.querySelectorAll('.tree-item').forEach(el => el.classList.remove('active'));
            element.classList.add('active');

            document.getElementById('code-box').innerText = "Loading " + filePath + "...";

            fetch('/api/file?path=' + encodeURIComponent(filePath))
                .then(res => res.text())
                .then(code => {
                    document.getElementById('code-box').innerText = code;
                })
                .catch(err => {
                    document.getElementById('code-box').innerText = "Failed to load " + filePath;
                });
        }

        function exportProject() {
            // Explain standard platform download via setting menu
            alert("To export and download this fully complete Android Studio project:\\n\\n1. Click the 'Settings' icon (gear) in the AI Studio header menu.\\n2. Click 'Export to ZIP' or 'Push to GitHub'.\\n3. Download and unzip the codebase on your local machine.\\n4. Open the root folder in Android Studio and click 'Run' to compile the real APK!");
        }

        function simulateShareSheet() {
            if (navigator.share) {
                navigator.share({
                    title: 'Naam Jap Reminder App',
                    text: 'Radha Vallabh Shri Harivansh! Maintain steady daily chanting with the Naam Jap Reminder offline app. Download it here: https://play.google.com/store/apps/details?id=com.naamjap.reminder'
                }).then(() => {
                    addLog('App link shared successfully');
                }).catch((err) => {
                    console.error(err);
                });
            } else {
                // Fallback alert
                alert("Simulating Android Share Sheet:\\n\\nSharing Link: https://play.google.com/store/apps/details?id=com.naamjap.reminder\\n\\nMessage: Radha Vallabh Shri Harivansh! Maintain steady daily chanting with the Naam Jap Reminder offline app.");
                addLog('App link shared via simulated Share Sheet');
            }
        }

        // Startup
        window.onload = function() {
            generateMalaDots();
            updateMalaUI();
            addLog('Application initialized in secure local storage');
            
            // Load default file (MainActivity.kt)
            const activeItem = document.querySelector('.tree-item.active');
            if (activeItem) {
                loadFile('app/src/main/kotlin/com/naamjap/reminder/MainActivity.kt', activeItem);
            }
        };
    </script>
</body>
</html>`;
}
