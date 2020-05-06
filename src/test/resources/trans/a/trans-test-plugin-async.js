import _asyncToGenerator from "@babel/runtime/helpers/asyncToGenerator";

function f() {
  return _f.apply(this, arguments);
}

function _f() {
  _f = _asyncToGenerator(function* () {
    return 1;
  });
  return _f.apply(this, arguments);
}