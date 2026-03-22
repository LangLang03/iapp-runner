export default function(hljs) {
  const KEYWORDS = {
    keyword: 'f w s n syso endcode return break continue else if',
    literal: 'true false null'
  }

  const BUILTINS = [
    'json', 'map', 'mget', 'mset', 'mkeys', 'mhas', 'arr', 'arrpush', 'length',
    'method', 'get', 'gets', 'post', 'posts', 'body', 'path', 'url', 'header',
    'clientip', 'useragent', 'isajax', 'isjson', 'getcookie', 'setcookie', 'delcookie',
    'text', 'html', 'status', 'error', 'redirect', 'setHeader',
    'db', 'dbone', 'dball', 'dbpage', 'dbcount', 'dbinsert', 'dbupdate', 'dbdelete', 'dbsearch', 'dbexec',
    'file', 'files', 'gfn', 'gfs', 'gft', 'gfe', 'sf',
    'register', 'login', 'verify', 'logout', 'hashpassword', 'verifypassword',
    'md5', 'sha256', 'hmacsha256', 'base64encode', 'base64decode', 'aesencrypt', 'aesdecrypt',
    'jwtencode', 'jwtdecode', 'jwtverify',
    'session', 'setsession', 'delsession', 'hassession',
    'mailconfig', 'sendmail',
    'env', 'loadenv',
    'cors', 'port', 'config', 'upc', 'info',
    'async', 'asyncwait'
  ]

  return {
    name: 'iapp',
    case_insensitive: true,
    keywords: KEYWORDS,
    contains: [
      {
        className: 'comment',
        begin: '#',
        end: '$',
        contains: [
          {
            begin: '\\w+'
          }
        ]
      },
      {
        className: 'string',
        begin: '"',
        end: '"',
        illegal: '\\n',
        contains: [
          {
            className: 'escape',
            begin: '\\\\[\\s\\S]'
          }
        ]
      },
      {
        className: 'number',
        begin: '\\b\\d+(\\.\\d+)?',
        relevance: 0
      },
      {
        className: 'built_in',
        begin: '\\b(' + BUILTINS.join('|') + ')\\b',
        relevance: 0
      },
      {
        className: 'variable',
        begin: '\\b[a-zA-Z_][a-zA-Z0-9_]*\\b',
        relevance: 0
      }
    ]
  }
}
