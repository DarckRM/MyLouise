import axios from "axios";
import router from "../router";
// axios.defaults.baseURL = "http://127.0.0.1:8099/saito/"; //此路径为配置代理服务器时的代理路径
axios.defaults.baseURL = import.meta.env.VITE_REQUEST_URL; //此路径为配置代理服务器时的代理路径

axios.interceptors.request.use(config => {
  // 在发送请求之前做些什么
  const token = window.localStorage.getItem('token');
  if (token) {
      config.headers.token = 'Bearer ' + token;
  }
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
})

export default {
  get(url, data, responseType) {
    // url: 接口；路径；data: 请求参数；responseType：相应的数据类型，不传默认为json
    return new Promise((resolve, reject) => {
      axios({
        method: "get",
        url,
        data,
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json; charset=utf-8",
          withCredentials: true,
        },
        //默认json格式，如果是下载文件，需要传 responseType:'blob'
        responseType:
          responseType == null || responseType == "" ? "json" : responseType,
      }).then((response) => {
        if (response.status == 200) {
          //根据实际情况进行更改
          resolve(response);
        } else {
          reject(response);
        }
      });
    });
  },
  post(url, data, responseType) {
    // url: 接口；路径；data: 请求参数；responseType：相应的数据类型，不传默认为json
    return new Promise((resolve, reject) => {
      axios({
        method: "post",
        url,
        data,
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json; charset=utf-8",
          withCredentials: true,
        },
        //默认json格式，如果是下载文件，需要传 responseType:'blob'
        responseType:
          responseType == null || responseType == "" ? "json" : responseType,
      }).then((response) => {
        if (response.status == 200) {
          //根据实际情况进行更改
          resolve(response);
        } else {
          reject(response);
        }
      });
    });
  },
};
