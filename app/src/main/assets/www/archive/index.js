let userId = ''
let nickname = ''
let file = {
  // file_name: 'test使用.xlsx',
  // file_type: 'xlsx',
  // file_download_url: 'https://api4.workplus.io/v1/domains/workplus/orgs/c8522121-c038-4de1-8e8e-cb8ab6a6d32f/pan/6164170e259244c6b9fa230012b5f330?access_token=c3f1fd5264ab4333b87df5ea9ef6dbbe',
  // file_id: '6164170e259244c6b9fa230012b5f330',
}

function getUrlParams() {
  let url = decodeURIComponent(window.location.href).split('?')[1];
  // const mockUrl =
  //   'http://172.16.1.25:8080/dist/index.html?ticket=fe6f920346d63881b6c3cf1003be457996e58a12&org_id=26e94695826b490db973b18961700dcd/';
  // let url = mockUrl.split('?')[1];
  if (!url) {
    return {};
  }
  if (url.indexOf('#!') >= 1) {
    url = url.substr(0, url.indexOf('#!'));
  }
  const query = {};
  let strs;
  if (url.indexOf('&') > -1) {
    strs = url.split('&');
    for (let i = 0; i < strs.length; i += 1) {
      // eslint-disable-next-line prefer-destructuring
      query[strs[i].split('=')[0]] = strs[i].split('=')[1];
    }
  } else {
    const key = url.substring(0, url.indexOf('='));
    const value = url.substr(url.indexOf('=') + 1);
    query[key] = decodeURI(value);
  }
  return query;
}

/**
 * 获取文件信息
 *
 */
function getFileInfo() {
  cordova.exec(function (result) {
      result = JSON.parse(result)
      console.log('result', result)
      file.file_name = result.file_name
      file.file_id = result.file_id
      file.file_type = result.file_type
      file.file_download_url = result.file_download_url
      openOnlyoffice()
    },
    function (error) {
      alert("调用失败");
    },
    'WorkPlus_Files',
    'filePreviewOnline',
    []
  );
}

/**
 * 如果为 workplus 环境，要cordova获取用户信息
 *
 */
function getUserInfo() {
  cordova.exec(
    function (result) {
      userId = result.user_id
      nickname = result.nickname
      getFileInfo()
    },
    function (error) {
      alert('用户信息获取失败')
    },
    'WorkPlus_Contact',
    'getCurrentUserInfo',
    []
  )
}

/**
 * cordova更换头部
 *
 */
function changeTitle() {
  cordova.exec(function (result) {},
    function (error) {},
    'WorkPlus_WebView',
    'title',
    ['onlyoffice']
  );
}

function clearRightButton() {
  cordova.exec(function (result) {},
    function (error) {},
    'WorkPlus_WebView',
    'clearRightButtons',
    []
  );
}

function openOnlyoffice() {
  let documentType = ''
  const textType = ['doc', 'docx', 'pdf', 'odt', 'txt']
  const sheetType = ['xlsx', 'xls', 'ods', 'csv']
  const presentType = ['pptx', 'ppt', 'odp']
  if (textType.indexOf(file.file_type) !== -1) {
    documentType = 'text'
  } else if (sheetType.indexOf(file.file_type) !== -1) {
    documentType = 'spreadsheet'
  } else if (presentType.indexOf(file.file_type) !== -1) {
    documentType = 'presentation'
  }
  console.log('doc', file, documentType, userId, nickname)
  if (documentType) {
    new DocsAPI.DocEditor('file_preview_wrapper', {
      type: 'mobile',
      document: {
        permissions: {
          edit: true,
        },
        key: file.file_id,
        fileType: file.file_type, // 展示文件的类型
        title: file.file_name,
        url: file.file_download_url //读取文件进行展示
      },
      documentType: documentType,
      editorConfig: {
        lang: 'zh-CN',
        // 回调接口，用于编辑后实现保存的功能,(关闭页面5秒左右会触发)
        callbackUrl: `http://172.16.1.55:9001/v1/only-office-callback/workplus?user_id=${userId}&username=${nickname}&name=${file.file_name}`,
        // callbackUrl: `http://dev23-api.workplus.io/v1/only-office-callback/workplus?user_id=${$scope.file.userId}&username=${$scope.file.username}&name=${fileName}`,
        user: {
          id: userId,
          name: nickname
        },
        customization: {
          showReviewChanges: true
        }
      },
    })
    return
  }
}

$(document).ready(function () {
  document.addEventListener('deviceready', function () {
    // getFileInfo()
    getUserInfo()
    changeTitle()
    clearRightButton()
    // openOnlyoffice()
  })
})