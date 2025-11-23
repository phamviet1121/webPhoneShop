(function() {
    // 1. CẤU HÌNH CSS (Giao diện)
    const styles = `
        :root { --primary-color: #4a90e2; --chat-bg: #ffffff; }
        .chat-widget-btn {
            position: fixed; bottom: 20px; right: 20px; width: 60px; height: 60px;
            background: var(--primary-color); border-radius: 50%;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2); cursor: pointer; z-index: 99999;
            display: flex; align-items: center; justify-content: center;
            transition: transform 0.3s ease;
        }
        .chat-widget-btn:hover { transform: scale(1.1); }
        .chat-container {
            position: fixed; bottom: 90px; right: 20px; width: 350px; height: 500px;
            background: var(--chat-bg); border-radius: 15px;
            box-shadow: 0 5px 30px rgba(0,0,0,0.15); display: none;
            flex-direction: column; z-index: 99999; font-family: sans-serif;
            animation: slideIn 0.3s ease forwards;
        }
        @keyframes slideIn { from { opacity: 0; transform: translateY(20px); } to { opacity: 1; transform: translateY(0); } }
        .chat-header { background: var(--primary-color); color: white; padding: 15px; display: flex; justify-content: space-between; align-items: center; border-radius: 15px 15px 0 0; font-weight: bold; }
        .chat-messages { flex: 1; padding: 15px; overflow-y: auto; background: #fff; }
        .message { margin-bottom: 10px; padding: 10px; border-radius: 10px; max-width: 80%; word-wrap: break-word; font-size: 14px; }
        .message.bot { background: #f1f0f0; align-self: flex-start; margin-right: auto; color: #333; }
        .message.user { background: var(--primary-color); color: white; align-self: flex-end; margin-left: auto; text-align: right; }
        .chat-input-area { padding: 10px; border-top: 1px solid #eee; display: flex; background: #fff; border-radius: 0 0 15px 15px; }
        .chat-input-area input { flex: 1; padding: 10px; border: 1px solid #ddd; border-radius: 20px; outline: none; }
        .chat-input-area button { margin-left: 5px; background: var(--primary-color); color: white; border: none; padding: 10px 15px; border-radius: 50%; cursor: pointer; }
        #loading { display: none; font-size: 12px; color: #888; padding: 5px 15px; font-style: italic; }
    `;

    // 2. CẤU HÌNH HTML (Khung Chat)
    const htmlStructure = `
        <div class="chat-widget-btn" id="chatToggle">
            <svg viewBox="0 0 24 24" width="30" height="30" fill="white"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/></svg>
        </div>
        <div class="chat-container" id="chatContainer">
            <div class="chat-header">
                <span>🤖 Trợ lý Phone Shop</span>
                <span style="cursor:pointer; font-size:20px;" id="chatCloseBtn">×</span>
            </div>
            <div class="chat-messages" id="chatBox">
                <div class="message bot">Xin chào! Em có thể giúp gì cho anh/chị ạ?</div>
            </div>
            <div id="loading">Đang soạn tin...</div>
            <div class="chat-input-area">
                <input type="text" id="userInput" placeholder="Nhập tin nhắn...">
                <button id="sendBtn">➤</button>
            </div>
        </div>
    `;

    // 3. TIÊM CSS VÀ HTML VÀO TRANG WEB
    const styleSheet = document.createElement("style");
    styleSheet.innerText = styles;
    document.head.appendChild(styleSheet);

    const chatWrapper = document.createElement("div");
    chatWrapper.innerHTML = htmlStructure;
    document.body.appendChild(chatWrapper);

    // 4. XỬ LÝ LOGIC (JAVASCRIPT)
    const chatContainer = document.getElementById('chatContainer');
    const chatToggle = document.getElementById('chatToggle');
    const chatCloseBtn = document.getElementById('chatCloseBtn');
    const userInput = document.getElementById('userInput');
    const chatBox = document.getElementById('chatBox');
    const sendBtn = document.getElementById('sendBtn');
    const loading = document.getElementById('loading');

    // API URL (Lưu ý: Nếu chạy từ web khác localhost, cần đổi localhost thành IP thật hoặc Domain)
    const API_URL = "http://localhost:8080/api/chat/ask"; 

    function toggleChat() {
        if (chatContainer.style.display === 'none' || chatContainer.style.display === '') {
            chatContainer.style.display = 'flex';
            chatToggle.style.display = 'none';
            userInput.focus();
        } else {
            chatContainer.style.display = 'none';
            chatToggle.style.display = 'flex';
        }
    }

    chatToggle.addEventListener('click', toggleChat);
    chatCloseBtn.addEventListener('click', toggleChat);

    // Tự đóng khi click ra ngoài
    document.addEventListener('click', function(e) {
        if (chatContainer.style.display === 'flex' && 
            !chatContainer.contains(e.target) && 
            !chatToggle.contains(e.target)) {
            toggleChat();
        }
    });

    async function sendMessage() {
        const msg = userInput.value.trim();
        if (!msg) return;

        // Hiện tin nhắn user
        appendMessage(msg, 'user');
        userInput.value = '';
        loading.style.display = 'block';

        try {
            const response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'text/plain' },
                body: msg
            });
            const text = await response.text();
            appendMessage(text, 'bot');
        } catch (err) {
            appendMessage("Lỗi kết nối server!", 'bot');
        } finally {
            loading.style.display = 'none';
        }
    }

    function appendMessage(text, sender) {
        const div = document.createElement('div');
        div.classList.add('message', sender);
        div.innerHTML = text.replace(/\n/g, '<br>');
        chatBox.appendChild(div);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    sendBtn.addEventListener('click', sendMessage);
    userInput.addEventListener('keypress', (e) => { if(e.key === 'Enter') sendMessage(); });

})();