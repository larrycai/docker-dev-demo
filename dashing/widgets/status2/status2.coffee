class Dashing.Status2 extends Dashing.Widget

  onData: (data) ->
    if data.items.length > 0
      $(@node).find('div.status-failed').show()
      $(@node).find('div.status-succeeded').hide()
      # $(@node).css("background-color", "red")
    else
      $(@node).find('div.status-failed').hide()
      $(@node).find('div.status-succeeded').show()
      $(@node).css("background-color", "#12b0c5")