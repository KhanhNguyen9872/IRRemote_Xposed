# Tóm Tắt Quá Trình Patch (Mod) Ứng dụng Oppo IR Remote

Dưới đây là tóm tắt toàn bộ quá trình phân tích và vá (patch) các lỗi để đưa ứng dụng **Oppo IR Remote** hoạt động trên các dòng máy Non-Oppo (khác Oppo/OnePlus/Realme), kèm mã Smali trích xuất trước và sau khi sửa.

## 1. Vá lỗi Crash ứng dụng: Thiếu Class `OplusFeatureConfigManager`

- **Hiện tượng**: App crash ngay khi mở. Logcat báo lỗi:
  ```java
  java.lang.NoClassDefFoundError: Failed resolution of: Lcom/oplus/content/OplusFeatureConfigManager;
  ```
- **Nguyên nhân**: Trên các ROM máy khác không có sẵn bộ thư viện `com.oplus.content`, dẫn đến gọi phương thức `hasFeature` bị ném lỗi.
- **Cách khắc phục**: Tạo mới hoàn toàn file `classes_smali/com/oplus/content/OplusFeatureConfigManager.smali` để giả lập class này trả về `true`.

**Mã Class tạo mới:**
```smali
.class public Lcom/oplus/content/OplusFeatureConfigManager;
.super Ljava/lang/Object;
.source "SourceFile"

.method public static constructor <clinit>()V
    .registers 0
    return-void
.end method

.method public constructor <init>()V
    .registers 1
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static getInstance()Lcom/oplus/content/OplusFeatureConfigManager;
    .registers 1
    new-instance v0, Lcom/oplus/content/OplusFeatureConfigManager;
    invoke-direct {v0}, Lcom/oplus/content/OplusFeatureConfigManager;-><init>()V
    return-object v0
.end method

.method public hasFeature(Ljava/lang/String;)Z
    .registers 3
    const/4 v0, 0x1
    return v0
.end method
```
---

## 2. Vá lỗi "No Network Connection" cục bộ (Local Checks)

- **Hiện tượng**: Khi ấn Add branch, ứng dụng báo "No network connection" do sai logic kiểm tra mạng.
- **Cách khắc phục**: Ép các hàm kiểm tra mạng cục bộ trả về `true` (thành công).

**File 1: Sửa class nội bộ** `classes2_smali/b7/j0.smali`
```diff
  .method public final a(Landroid/content/Context;)Z
-     .registers 6
-     ... (mã kiểm tra ConnectivityManager lằng nhằng dài dòng)
-     return p1
+     .registers 3
+     const/4 v0, 0x1
+     return v0
  .end method
  
  .method public final b(Landroid/content/Context;)Z
-     .registers 5
-     ... (kiểm tra VPN, IP rườm rà)
-     return p1
+     .registers 3
+     const/4 v0, 0x1
+     return v0
  .end method
```

**File 2: Sửa SDK Kookong** `classes2_smali/com/kookong/sdk/ir/c1.smali`
```diff
  .method public static b()Z
-     .registers 2
-     invoke-static {}, Lcom/kookong/sdk/ir/c1;->a()Landroid/net/NetworkInfo;
-     move-result-object v0
-     if-eqz v0, :cond_c
-     ...
-     return v0
+     .registers 1
+     const/4 v0, 0x1
+     sput-boolean v0, Lcom/kookong/sdk/ir/c1;->a:Z
+     return v0
  .end method
```
---

## 3. Vá lỗi Kookong SDK chặn hãng thiết bị (Hardware API Filter)

- **Hiện tượng**: Máy chủ phát hiện máy không mang ID của hãng OPPO nên từ chối không cho gọi APIs tải Database.
- **Cách khắc phục**: Sửa đổi `classes2_smali/com/kookong/sdk/ir/m3.smali` để luôn truyền thông tin `MODEL` và `MANUFACTURER` là OPPO.

**Code Smali trước lúc Patch:**
```smali
    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;
    sput-object v0, Lcom/kookong/sdk/ir/m3;->f:Ljava/lang/String;

    sget-object v0, Landroid/os/Build;->MANUFACTURER:Ljava/lang/String;
    sput-object v0, Lcom/kookong/sdk/ir/m3;->g:Ljava/lang/String;
```

**Code Smali SAU KHI Patch:** (Áp dụng cho 2 vị trí tĩnh `a(Context)` và static init)
```diff
-   sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;
+   const-string v0, "OPPO"
    sput-object v0, Lcom/kookong/sdk/ir/m3;->f:Ljava/lang/String;

-   sget-object v0, Landroid/os/Build;->MANUFACTURER:Ljava/lang/String;
+   const-string v0, "OPPO"
    sput-object v0, Lcom/kookong/sdk/ir/m3;->g:Ljava/lang/String;
```

> [!WARNING]
> Mở file `m3.smali` đang trực tiếp có trên Editor của bạn. Bạn vừa vô tình ấn enter chèn vào hơn 300 dòng trắng và chuỗi chữ rác `ưu6tri` ở cuối mảng code patch. Hãy vào sửa xóa bỏ những ký tự và dòng trắng thừa đó đi trước khi build bằng Apktool nhé!

## 4. Fix lỗi JNI C++ chặn Request (`d2=` trống) - Bypass Signature C++

- **Hiện tượng**: Máy chủ trả về lỗi `[0, "code 1"]` do request `d2=` bị rỗng (do thư viện bảo mật native `libkksdk.so` phát hiện App đã bị thay đổi chữ ký không còn là của Oppo).
- **Cách xử lý triệt để**:
  - Do `libkksdk.so` gọi lớp `PackageManager` móc chữ ký bằng native C++ (JNI). Bạn hãy Re-compile Smali thành file **.apk** bình thường.
  - Mang qua Tool **NP Manager** chọn *Super Signature Bypass* hoặc **MT Manager** chọn *Kill Signature*. 
  - Tool sẽ tự động tạo một lớp Java Reflection để giả lập trả về Certificate.RSA zin nguyên tấm, lừa JNI là máy chưa hề bị sửa.

=> Lúc này JNI sẽ thực thi Encrypt trả về 1 chuỗi mã hoá dài cho `d2`, Kookong mở khoá và Branch sẽ hoạt động ngon ơ! 
