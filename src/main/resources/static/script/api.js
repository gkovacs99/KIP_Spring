const API = {

    post: async (url, data) => {
        return sendRequest(url, "POST", data);
    },

    get: async (url) => {
        return sendRequest(url, "GET");
    }

};

const sendRequest = async (url, method, data = null) => {

    try {
        const options = {
            method: method
        };

        if (data) {
            options.headers = {
                "Content-Type": "application/x-www-form-urlencoded"
            };

            options.body = data;
        }

        const response = await fetch(url, options);
        const result = await response.text();

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}: ${result}`);
        }

        return result;

    } catch (error) {
        console.error("API hiba:", error);
        throw error;
    }
};

const redirect = (href = "/", history = true) => {
    if (history) {
        window.location.assign(href);
        return;
    }
    window.location.replace(href);
}