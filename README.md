# Xposed Module Maker
[![GitHub License](https://img.shields.io/github/license/xzakota/XposedModuleMaker?color=blue)](https://github.com/xzakota/XposedModuleMaker/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.xzakota.xposed/annotation?color=green)](https://search.maven.org/search?q=g:com.xzakota.xposed)

快速配置 [Xposed](https://api.xposed.info) 模块**元信息**。利用 Gradle 插件实现入口类的检测写入和其他资源的创建合并。
> 仅针对元信息配置，不包含 HOOK 代码实现

# 支持
- Legacy API 元信息
- [LSP Modern API](https://github.com/LSPosed/LSPosed/wiki/Develop-Xposed-Modules-Using-Modern-Xposed-API) 元信息

# 使用
在 app 目录下的 `build.gradle.kts` 添加

## 插件及依赖
```
plugins {
    id("com.xzakota.xposed") version "${version}"
}

dependencies {
    compileOnly("com.xzakota.xposed:annotation:${version}")
}
```

## 配置一(入口类)
`Legacy API`

```Kotlin
import com.xzakota.xposed.annotation.ModuleEntry
import de.robv.android.xposed.IXposedHookLoadPackage

// 添加注解
@ModuleEntry
class XPInitEntry : IXposedHookLoadPackage 
```

`LSP Modern API`

```Kotlin
import com.xzakota.xposed.annotation.ModuleEntry
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

// 添加注解
@ModuleEntry
class LSPInitEntry(base : XposedInterface, param : XposedModuleInterface.ModuleLoadedParam) : XposedModule(base, param) 
```

## 配置二
|          字段           |     作用      |
|:---------------------:|:-----------:|
|    isXposedModule     | 控制插件的启用/禁用  |
|     minAPIVersion     |  最低 API 版本  |
|      description      |    模块介绍     |
|         scope         | 作用域(需管理器支持) |
| isIncludeDependencies |   是否检索依赖库   |
| isGenerateConfigClass |   是否生成信息类   |

|     扩展方法     |          作用           |
|:------------:|:---------------------:|
| description  |   多国语言的模块介绍(会覆盖字段值)   |
|  framework   |        想支持的框架         |
|   lsposed    | 针对 LSP Modern API 的配置 |
|    scope     |      作用域(需管理器支持)      |
| resGenerator |       针对资源生成的配置       |

具体值按需填写，提供一份详细举例
```
xposedModule {
    isXposedModule = true
    minAPIVersion = XposedAPIVersion.XP_API_82
    
    // description = "Xposed Example"
    description {
        // 默认介绍
        resString("Xposed Example")
        // 中文介绍
        resString("一个 Xposed 模块样例", LangCode.LANG_CODE_ZH_CN)
    }
    
    // 默认 XposedFramework.XPOSED
    framework {
        // 添加
        add(XposedFramework.LSPOSED)
        // 删除
        // remove(XposedFramework.XPOSED)
        // 只支持
        // only(XposedFramework.LSPOSED)
    }
    
    lsposed {
        // 新共享首选项(将在 LSPosed-2.1.0 停止支持)
        isNewXSharedPreferences = false
        // 目标 API 版本
        targetAPIVersion = XposedAPIVersion.XP_API_100
        // 静态作用域
        isStaticScope = true
    }
    
    resGenerator {
        // 资源 ID 名称
        resID {
            descriptionResID = "xposed_module_description"
            scopeResID = "xposed_module_scope"
        }
    }

    scope += listOf(
        "com.android.settings"
    )
    
    isIncludeDependencies = false
    isGenerateConfigClass = true
}
```
另可参考 [example](https://github.com/xzakota/XposedModuleMaker/tree/main/example) 模块

# TODO
- [ ] Native 入口文件
- [ ] 配套使用的 HOOK 工具库