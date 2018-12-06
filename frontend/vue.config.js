module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8088',
        ws: true, // proxy websockets?
        changeOrigin: true
      }
    }
  }
}
