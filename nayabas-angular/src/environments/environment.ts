export const environment = {
  production: false,

  apiUrl: ''   //new changee
  // apiUrl: 'http://localhost:8080'
  // apiUrl: 'http://192.168.165.62:8080'  // ✅ Use your laptop IP

  
    // CRITICAL FIX: Changed from 'http://localhost:8080' to your network IP.
  // The mobile device will now correctly send API requests to your laptop's Spring Boot server.
    // apiUrl: 'http://192.168.165.113:8080'

};


//ng serve --host 0.0.0.0


//ng serve --host 0.0.0.0 --proxy-config proxy.conf.json
