function parser(fn)
{
  var tmp;

  if (fn.substring(0, 3) == "../") {
    tmp = location.href.lastIndexOf("/");
    if(tmp >= 0) {
      location.href = location.href.substring(0,tmp) + "/" + fn;
    }
    else {
      location.href = location.href + fn;
    }
  }
  else {
    location.href = fn;
  }
}
