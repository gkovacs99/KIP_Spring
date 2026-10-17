fetch("/test")
    .then(response => {
        console.log("HTTP státusz:", response.status);
        return response.text();
    })
    .then(text => {
        console.log("Kapott szöveg:", text);
    })
    .catch(error => {
        console.error("Fetch hiba:", error);
    });