<<<<<<< HEAD
# 头像上传功能封装
--------

![image](https://github.com/noiary/upload-image/demo.gif)
## 两种模式：
1. 选取头像，默认剪切尺寸为260px,可以选取剪切区域
2. 上传图片，不需要剪切，原图比例直接上传，上传之前会进行bitmap压缩，大概会控制在200KB左右

=======
## 注意事项
 1. 权限问题，如果是6.0以上系统，可能需要手动授权存储空间
 2. 在调取uploadAdapter的页面，监听onActivityResult方法，并执行uploadAdapter.onResult(resultCode,resultCode,intent)
