# ChatGPT 安卓版 - 私人定制 AI

私人定制 AI，只要在 App 里设置你的 Openai API Key，就可以随时随地和 AI 聊天，并且支持上下文对话，聊天历史随时查看。

开源版本是直接调用的 Openai API 接口，所以还需要挂 VPN。如何不想挂 VPN 使用，可以试试商用版本 或是 自己搭建反向代理服务。

> 安全提示：  
> 1、API Key 只存在本地（如果二次开发请不要把 key 上传到开源仓库）  
> 2、APP 聊天历史只存在本地

## 功能

- 聊天/绘画功能
    - OpenAI API: GPT-3.5/GPT-4（是否支持GPT4，依赖你的 API Key）
- 语音（商用版支持）
    - Azure Speech SDK: 语音切换

## 配置

- OpenAI API Key

### 两种方式设置 API Key：

#### 1、可以在代码里设置

```kotlin
package com.openai.api.aichat.common.utils

const val OPENAI_API_KEY = ""
```

#### 2、APP设置里动态设置

<img src="images/chatgpt_60.jpeg" width="50%">

## App 截图

### 开源版本

- 聊天
- 绘画
- 历史

<img src="images/chatgpt_1.jpeg" width="30%"> <img src="images/chatgpt_2.jpeg" width="30%"> <img src="images/chatgpt_3.jpeg" width="30%"> 
<img src="images/chatgpt_4.jpeg" width="30%"> <img src="images/chatgpt_5.jpeg" width="30%">

### 商用版本（无需 VPN，直接使用）

- 聊天
- 绘画
- 历史
- 语音（定制化语音学习外语） 
- 支付（支付宝支付）

**目前支持外语语音：**  
外语：
  - 🇺🇸英语
  - 🇪🇸西班牙语
  - 🇦🇪阿拉伯语
  - 🇫🇷法语
  - 🇷🇺俄语
  - 🇵🇹葡萄牙语
  - 🇩🇪德语
  - 🇯🇵日语 
  
小语种：
  - 🇰🇷韩语，
  - 🇮🇹意大利语
  - 🇳🇱荷兰语
  - 🇵🇱波兰语
  - 🇸🇪瑞典语
  - 🇺🇦乌克兰语
  - 🇹🇷土耳其语
  - 🇬🇷希腊语

<img src="images/aichat_1.jpeg" width="30%"> <img src="images/aichat_2.jpeg" width="30%"> <img src="images/aichat_3.jpeg" width="30%">
<img src="images/aichat_4.jpeg" width="30%"> <img src="images/aichat_5.jpeg" width="30%">

## App 下载

### 商用版本

下载地址：[https://www.pgyer.com/Fq458k](https://www.pgyer.com/Fq458k)  
或  
二维码  
<img src="images/qrcode_download.png" width="30%" height="30%">

## 赞助

如果项目对您有帮助，希望老板支持一下，祝老板发财～

微信打赏  
<img src="images/wexinpay.jpeg" width="30%" height="30%">

支付宝打赏  
<img src="images/alipay.jpeg" width="30%" height="30%">

## 商务合作

扫码加微，非诚勿扰，非常感谢～  
<img src="images/wechat_qr.jpeg" width="30%" height="30%">

## License

MIT License

Copyright (c) 2023 Ouyang Jinmiao

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.