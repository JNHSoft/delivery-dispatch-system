module.exports = {
    apps: [{

        name: "ddetw",
        script: "./server.js",
        exec_mode: "cluster",
        instances: 4,
        autorestart: true,
        watch: false,
        max_memory_restart: '1G',
        
        env_production: {
            "PORT": 3000,
            "APP_ENV": "production",
            "NODE_ENV": "production"
        }
    }]
};